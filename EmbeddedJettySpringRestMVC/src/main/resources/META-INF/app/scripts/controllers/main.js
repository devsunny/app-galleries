'use strict';
/**
 * @ngdoc function
 * @name sbAdminApp.controller:MainCtrl
 * @description
 * # MainCtrl
 * Controller of the sbAdminApp
 */
angular.module('sbAdminApp')
  .controller('MainCtrl', ['$scope', '$http', '$interval', '$position', function($scope, $http, $interval,$position) {
	  $interval(function(){
		  $http.get('/spring/dashboard.json').then(function(json) {			  
			  $scope.dashboard = json.data.payload;
	      });		  
	  }, 2000);	  
  }])  
  ;
