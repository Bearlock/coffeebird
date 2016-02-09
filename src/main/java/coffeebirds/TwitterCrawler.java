package coffeebirds;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Date;
import java.util.Scanner;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.scribe.builder.ServiceBuilder;
import org.scribe.builder.api.TwitterApi;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Token;
import org.scribe.model.Verb;
import org.scribe.oauth.OAuthService;

import org.apache.log4j.Logger;



/**Gathers a String from the user, and then finds Tweets related to that string, via the
 * Twitter API.  The class then clusters the Tweets, using the RxNLP API (through mashape).
 * Finally, the clustered results are output to the screen, to show what people are Tweeting
 * about the topic entered by the user.
 * @author Ryan Huntbach
 * Slightly modified for external use by Ben Berntson
 * Added logging capability by Ben Berntson
 *
 */
public class TwitterCrawler
{
    static Logger log = Logger.getLogger(TwitterCrawler.class.getName());

    private final String promptUserForInputMessage = "Enter a word, or words, you'd like to find Tweets for: ";
    private String wordsToFind;
    private int totalTweetsToGather = 100;
    private String jsonString = null;
    private String nlpResponseString;
    private TweetObj[] tweetObjArr = new TweetObj[totalTweetsToGather];
    private int tweetsCount = 0;

    public TwitterCrawler(String wordsToFind){
        this.wordsToFind = wordsToFind;
    }


    /**Handles control flow for the application
     * @author Ryan Huntback
     * @return void
     */
    public String run(){
        listenForTweets();
        buildJsonString();
        getClusters();
        parseAndOutputClusteringAPIResponse();
        return nlpResponseString;
    }


    /**Creates a JSON object that holds the data for "created", "text", "name" and "screen_name"
     * @author Ryan Huntback
     * @return void
     * @throws IOException
     * @throws JsonMappingException
     * @throws JsonParseException
     */
    private boolean parseTweet(String tweetData) throws JsonParseException, JsonMappingException, IOException{
        log.info("Cleaning raw tweet: " + tweetData + new Date().toString());
        if (tweetData.isEmpty()){ return false; }//Sometimes Twitter API sends blank Tweets

        try{

            //Convert JSON into an object
            ObjectMapper mapper = new ObjectMapper();
            tweetObjArr[tweetsCount] = mapper.readValue(tweetData, TweetObj.class);

            //Remove unwanted text
            String textStr = tweetObjArr[tweetsCount].getText();
            textStr = textStr.replaceAll("\\bhttp\\S+", "");
            textStr = textStr.replaceAll("@\\S+", "");
            textStr = textStr.replaceAll("\\#\\S+", "");
            textStr = textStr.replaceAll("RT ", "");
            textStr = textStr.replaceAll("\"", "");
            textStr = textStr.replaceAll("\\n", "");
            textStr = textStr.replaceAll("[^a-zA-Z0-9\\s]", "");

            log.info("got here with text str"+ textStr);

            //Is the text still useful now that the unwanted text was removed?
            if (textStr.length() > 0){
                tweetObjArr[tweetsCount].setText(textStr);
                return true;
            }else{
                tweetObjArr[tweetsCount] = null;
                return false;
            }

        }catch(JsonParseException e){//frogger add multi-catch statement
            log.info("Encountered exception: ", e);
            System.out.println("Encountered JsonParseException in parseTweet()\n");
            System.out.println(tweetData);
            e.printStackTrace();
            return false;
        }catch(JsonMappingException e){
            log.info("Encountered exception: ", e);
            System.out.println("Encountered JsonMappingException in parseTweet()\n");
            System.out.println(tweetData);
            e.printStackTrace();
            return false;
        }catch(IOException e){
            log.info("Encountered exception: ", e);
            System.out.println("Encountered IOException in parseTweet()\n");
            System.out.println(tweetData);
            e.printStackTrace();
            return false;
        }
    }

    /**Builds a JSON string from an array of Strings
     * @author Ryan Huntbach
     * @return void
     */
    private void buildJsonString(){

        StringBuilder myJsonStringBuilder = new StringBuilder("{\"type\":\"pre-sentenced\",\"text\":[");

        for (int tweetNum = 0; tweetNum < totalTweetsToGather; tweetNum++){
            myJsonStringBuilder.append(String.format("{\"sentence\":\"%s\"},", tweetObjArr[tweetNum].getText()));
        }

        myJsonStringBuilder.delete(myJsonStringBuilder.length() - 1, myJsonStringBuilder.length());//remove the last comma
        myJsonStringBuilder.append("]}");
        jsonString = myJsonStringBuilder.toString();
    }

    /**Calls the RxNLP API, captures the response, and returns the response as a string
     * @author Ryan Huntbach
     * @return void
     */
    private void getClusters(){

        final String NLP_URI = "https://rxnlp-core.p.mashape.com/generateClusters";

        //Call RxNLP API
        try{

            //Create and send request to RxNLP API
            OAuthRequest nlp_request = new OAuthRequest(Verb.POST, NLP_URI);
            nlp_request.addHeader("X-Mashape-Authorization", "Zf4RxRjAw7mshPxtFeX2i2WafJDcp1mv13cjsnTW2QUPmOBaFJ");//read file and assign keys to strings
            nlp_request.addHeader("Content-Type", "application/json");
            nlp_request.addHeader("Accept", "application/json");
            nlp_request.addPayload(jsonString);
            Response nlp_response = nlp_request.send();

            //capture nlp_response and convert to string
            String line;
            BufferedReader reader = new BufferedReader(new InputStreamReader(nlp_response.getStream()));
            while ((line = reader.readLine()) != null){
                nlpResponseString += line;
            }

        }catch (IOException e) {
            log.info("Encountered exception: ", e);
            log.info("Stack trace: ", e);
            System.out.println("Encountered a problem while reading from the RxNLP API.  Program must close.");
            System.out.println("JSON String: " + jsonString);
            e.printStackTrace();
        }
    }


    /**Listens to live Twitter feed, via the Twitter API, to find Tweets related to "wordsToFind" (TwitterCrawler::private static string).
     * As each Tweet comes in, this function calls the parseTweet method to clean the Tweet.
     * If parseTweet returns a valid tweet (contains valid text), then tweet is added to "tweetsArr" (TwitterCrawler::private static String[]).
     * @author Ryan Huntbach
     * @return void
     *
     * Logger added by Ben
     *
     */
    private void listenForTweets()
    {
        log.info("Listening to live Twitter feed.  This will take some time...");

        try{

            final String TWITTER_STREAM_URI = "https://stream.twitter.com/1.1/statuses/filter.json";

            //open link to Twitter API
            OAuthService twitter_API_service = new ServiceBuilder()
                    .provider(TwitterApi.class)
                    .apiKey("rFyY96RRHALFkhAaulYYZIeCD")
                    .apiSecret("vqTWzhUD4S1E1ZTnC1kd1H2hveYxV4E7FjH4Ev2s898rpxqGDP")
                    .build();
            Token accessToken = new Token("3916948993-Je2ynSs0449yBHxNhrAzty13NMWz4ifEXUqRDc2", "xQtald1ueBje3NgrVEz0fuC82MOSPc0XxU3DIce3lj5or");
            OAuthRequest twitter_API_request = new OAuthRequest(Verb.POST, TWITTER_STREAM_URI);
            twitter_API_request.addHeader("version", "HTTP/1.1");
            twitter_API_request.addHeader("host", "stream.twitter.com");
            twitter_API_request.setConnectionKeepAlive(true);
            twitter_API_request.addHeader("user-agent", "Twitter Stream Reader");
            twitter_API_request.addBodyParameter("track", wordsToFind);
            twitter_API_service.signRequest(accessToken, twitter_API_request);
            Response twitter_response = twitter_API_request.send();

            //Parse tweets and add them to tweetArr
            String rawTweetData;
            BufferedReader reader = new BufferedReader(new InputStreamReader(twitter_response.getStream()));
            while ((rawTweetData = reader.readLine()) != null && tweetsCount < totalTweetsToGather){
                if(parseTweet(rawTweetData)) {
                    ++tweetsCount;
                }
            }

        }catch (IOException e){
            log.info("Encountered exception while getting data from the twitter API: ", e);
            System.out.println("Encountered a problem while reading from the Twitter API.  Program must close.");
            e.printStackTrace();
        }
    }

    /**Parses Tweet clustering API response, and outputs results to the screen
     * @author Ryan Huntbach
     * @params void
     * @returns void
     */
    private void parseAndOutputClusteringAPIResponse(){
        log.info("SUCCESSFULLY RECEIVED RESPONSE FROM MASHAPE: " + nlpResponseString + "on" + new Date().toString());
    }
}