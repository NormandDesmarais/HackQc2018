var app = angular.module('appController', [])

app.controller('mainController', ['$scope', 'Request', function($scope, Request) {

    $scope.risques = [
        {
            show: true,
            title: "Inondations",
            icon: "icon_goutte.png"
        },
        {
            show: false,
            title: "Feu",
            icon: "icon_feu.png"
        }
    ]

    Request.get().then(function(data){
        console.log(data.data.file)
    })

}]);
