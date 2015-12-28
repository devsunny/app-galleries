'use strict';

/**
 * @ngdoc directive
 * @name izzyposWebApp.directive:adminPosHeader
 * @description
 * # adminPosHeader
 */
angular.module('sbAdminApp', [])	
	.controller('JobNotiCtrl', ['$scope', '$http', '$interval',  function($scope, $http, $interval) {
	  $scope.job = {
	    name: 'demo job 1',
	    number: 0
	  };
	  
	 // $interval(function(){
	//	  $http.get('/spring/s1.json').then(function(json) {
	//		  console.log(json.data.payload);
	//		  $scope.job.number = json.data.payload;
	 //     });		  
	 // }, 2000);
	  
	}])
	.directive('jobnotifications',function(){
		return {
        templateUrl:'scripts/directives/jobnotifications/jobnotifications.html',
        restrict: 'E',
        replace: true,
    	}
	});


