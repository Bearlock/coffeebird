(function() {
	var app = angular.module("coffeebirds", ["ngtweet"]);
	
	var MainController = function($scope, $http) {
	
		var searchTerm = "trump";
		
		$scope.embedTest = {
			'tweetid' : '617749885933232128'
		};
	
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
