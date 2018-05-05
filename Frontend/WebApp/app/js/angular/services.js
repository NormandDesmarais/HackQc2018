var app = angular.module('appServices', [])

app.factory('Request', ['$http', function($http) {
    return {
			get : function(type){
                if(type == "Inondations")
                    return $http.get('https://hackqc.herokuapp.com/api/JSON');
            }
        }
}]);
