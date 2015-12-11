'use strict';

/**
 * @ngdoc directive
 * @name izzyposWebApp.directive:adminPosHeader
 * @description
 * # adminPosHeader
 */
angular.module('sbAdminApp', [])	
	.controller('JobNotiCtrl', ['$scope', function($scope) {
	  $scope.job = {
	    name: 'demo job 1'
	  };
	}])
	.directive('jobnotifications',function(){
		return {
        templateUrl:'scripts/directives/jobnotifications/jobnotifications.html',
        restrict: 'E',
        replace: true,
    	}
	});


