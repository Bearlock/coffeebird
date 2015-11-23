package coffeebirds;

import java.io.*;
import java.util.concurrent.atomic.AtomicLong;


import java.io.*;
import java.sql.SQLException;
import java.util.*;


import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.json.XML;
import org.json.JSONObject;
import org.apache.log4j.Logger;
/**
 * Created by ben on 11/20/15.
 */
@RestController
@RequestMapping(value ="tweet_listener")


/**
 * @author Ben Berntson
 */
public class ListenerController {
    static final String xmlFileName = "output.xml";
    private static final String template = "Hello, %s!";
    private final AtomicLong counter = new AtomicLong();
    static Logger log = Logger.getLogger(ListenerController.class.getName());

    /**
     * @author Ben Berntson
     * @param searchTerm
     * @return
     */
    @RequestMapping(value="/greeting")
    public SimpleJsonPOJO greeting(@RequestParam(value="search_Term", defaultValue="trump") String searchTerm){
        log.info("Received http request from client, looking to search by: " + searchTerm);

        System.out.println(searchTerm);

        //instantiates an object of type TwitterCrawler and generates a JSON string for nlp's response
        TwitterCrawler tc = new TwitterCrawler(searchTerm);
        String result = tc.run();

        //checks to see if the first for characters of Mashape's response is "null" and removes "null" if it is
        if(result.substring(0,4).equals("null"))
            result = result.substring(4);

        //converts Mashape result to XML
        JSONObject jsonObject = new JSONObject(result);
        String xml = XML.toString(jsonObject);

        //writes out xml
        try {
            PrintWriter pw = new PrintWriter(new FileWriter(new File(xmlFileName)));
            pw.write(xml);
            pw.close();
        }
        catch(IOException e){
            System.out.println("Error while printing our XML file: " + xmlFileName + ".");
            e.printStackTrace();
        }

        //attempts to convert Mashape json to nlpResponseObj using a jackson mapper
        ObjectMapper mapper = new ObjectMapper();
        try {
            NLPResponseObj nlpResponseObj = mapper.readValue(result, NLPResponseObj.class);
        }
        catch(IOException e){
            System.out.println("Could not parse NLP/Mashape resonse.");
            e.printStackTrace();
        }

        //returns a simple Json object to the client that contains Mashape's response
        return new SimpleJsonPOJO(counter.incrementAndGet(), String.format(template, result));
    }

}
