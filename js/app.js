

var psApp = angular.module('psApp',[]);

psApp.service('ResponderList', function ($http, $q) { 
	return {
	  getData: function(jsonUrl) {
	      var deferred = new $q.defer();
	      $http.get(jsonUrl).success(function (resp) {
	          deferred.resolve(resp);
	      }).error(function(error) {
	          deferred.reject(error);
	      });
	      return deferred.promise;
	  } 
	}
});

psApp.service('ResponderMovement', function ($http, $q) { 
	return {
	  getData: function(jsonUrl,eventID) {
	      var deferred = new $q.defer();
	      $http({
	      	url:jsonUrl,
		  	method:'POST',
		  	data:{EventID:1} }).success(function (resp) {
	          deferred.resolve(resp);
	      }).error(function(error) {
	          deferred.reject(error);
	      });
	      return deferred.promise;
	  } 
	}
});


psApp.controller('monitorController', function($scope, ResponderList, ResponderMovement, $http){
	$scope.start = true;
	$scope.roster = false;
	$scope.overwatch = false;
	$scope.responders = [];
	$scope.rostered = [];
	$scope.OrganizationID = 1;
	$scope.movements = [];
	
	$scope.createEvent = function(){
		$scope.start = false;
		$scope.roster = true;
		$scope.getResponders();
		
		$http({
			url:'http://dev.publicsafetyapp.com:8070/api/event/create',
			method:'GET'
			}).success(function(resp){
				//$scope.eventID = resp.EventID;
				$scope.eventID = 1;
			}).error(function(error){
				console.log(error);
			});
	}
	
	$scope.getResponders = function(){
		url = 'http://dev.publicsafetyapp.com:8070/api/responder/list?OrganizationID=' + $scope.OrganizationID;
		ResponderList.getData(url).then(
	      function (data) {	
			  $scope.responders = data;
	      }, 
	      function (error) {
	        console.log('something went wrong');
	    }); 
	 }
	    
    $scope.addToRoster = function(responderID){
		$http({
			url:'http://dev.publicsafetyapp.com:8070/api/roster/add',
			method:'POST',
			data:{ResponderID:responderID,EventID:$scope.eventID}
		}).success(function(resp){
			console.log('did it!');
			console.log(resp);
		}).error(function(error){
			console.log(error);
		});
    }
    
    $scope.overwatch = function(){
    	$scope.roster = false;
    	$scope.overwatch = true;
    	
    	$scope.getMovement();
    }
    
    $scope.getMovement = function(){
    	url = 'http://dev.publicsafetyapp.com:8070/api/responder/get-movement'
    	ResponderMovement.getData(url,$scope.eventID).then(
    		function(data){
    			console.log(data);
    			$scope.movements = data;
    		},
    		function(error){
    			console.log('something went wrong');
    		}
    	)
    }

});

$(document).ready(function(){
	function millisecondsToStr (milliseconds) {
	    // TIP: to find current time in milliseconds, use:
	    // var  current_time_milliseconds = new Date().getTime();
	
	    function numberEnding (number) {
	        return (number > 1) ? 's' : '';
	    }
	
	    var temp = Math.floor(milliseconds / 1000);
	    var years = Math.floor(temp / 31536000);
	    if (years) {
	        return years + ' yr' + numberEnding(years);
	    }
	    //TODO: Months! Maybe weeks? 
	    var days = Math.floor((temp %= 31536000) / 86400);
	    if (days) {
	        return days + ' day' + numberEnding(days);
	    }
	    var hours = Math.floor((temp %= 86400) / 3600);
	    if (hours) {
	        return hours + ' hr' + numberEnding(hours);
	    }
	    var minutes = Math.floor((temp %= 3600) / 60);
	    if (minutes) {
	        return minutes + ' min' + numberEnding(minutes);
	    }
	    var seconds = temp % 60;
	    if (seconds) {
	        return seconds + ' sec' + numberEnding(seconds);
	    }
	    return 'less than a second'; //'just now' //or other string you like;
	}

	$(document).keyup(function(){
		$('.indicator').each(function(){
			
			var date1 = new Date($(this).data('time')); // 9:00 AM
			var date2 = Date.now();
			
			var diff = date2 - date1;
			$(this).children('span').text(millisecondsToStr(diff));
			
		});
	});
});