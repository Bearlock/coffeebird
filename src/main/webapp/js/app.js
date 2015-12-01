(function() {
	var app = angular.module("coffeebirds", []);
	
	var MainController = function($scope, $http) {
	
		var searchTerm = "trump";
		
		$scope.embedTest = {
			"html":'\u003Cblockquote class=\"twitter-tweet\"\u003E\u003Cp\u003EWhy I joined Twitter - Andy Piper \u003Ca href=\"https:\/\/twitter.com\/andypiper\"\u003E@andypiper\u003C\/a\u003E, Developer Advocate \u003Ca href=\"https:\/\/t.co\/fQ796U9lq1\"\u003Ehttps:\/\/t.co\/fQ796U9lq1\u003C\/a\u003E\u003C\/p\u003E— TwitterDev (<a href="https://twitter.com/intent/user?screen_name=TwitterDev">@TwitterDev</a>) \u003Ca href=\"https:\/\/twitter.com\/TwitterDev\/statuses\/482281320232415232\"\u003EJune 26, 2014\u003C\/a\u003E\u003C\/blockquote\u003E\n\u003Cscript async src=\"\/\/platform.twitter.com\/widgets.js\" charset=\"utf-8\"\u003E\u003C\/script\u003E'
		}
	
		var onUserComplete = function(response) {
			$scope.tog = false;
			console.log("Got to usercomplete");
			$scope.tweets = response.data;
			var clean = $scope.tweets.content;
			clean = clean.replace("Hello, ", "");
			clean = clean.replace("!", "");
			$scope.cleanTweets = angular.toJson(angular.fromJson(clean), true);
			clean = angular.fromJson(clean);
			$scope.clusters = clean.results.clusters;
			console.log(response.data);
			console.log($scope.tweets);
			console.log($scope.cleanTweets);
			$scope.error = "";

			//var updateContentDivWithData = function(data){
				//$('#loading').hide();
    			//document.getElementById('content').innerHTML = JSON.stringify(data);
//}
		};
	
		var onError = function(reason) {
			alert("PROBLEM");
			$scope.error = "Could not fetch tweet";
			$scope.tweets = NULL;
			$scope.tog = false;
		};
		
	
	
		$scope.search = function(searchTerm) {
			$scope.tog = true;
			console.log("Got here: " + searchTerm);
			var req = {
				url: '/tweet_listener/greeting',
	       	 	method: 'POST',
	        	headers: {
	            	'Accept':'application/json',
	            	'Content-Type': 'application/x-www-form-urlencoded'
	       		},
	        	dataType: 'json',
	        	data: "search_Term="+ searchTerm
			};
			console.log(req);
			$http(req).then(function(response){onUserComplete(response);}, function(){onError;});
		};
	};

	app.controller("MainController", ["$scope", "$http", MainController]);

}());
