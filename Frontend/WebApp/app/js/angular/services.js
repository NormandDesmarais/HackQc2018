var app = angular.module('appServices', [])

app.factory('Request', ['$http', function($http) {
    return {
			get : function(){
                return $http.get('https://hackqc.herokuapp.com/api/JSON');
            }
        }
}]);
