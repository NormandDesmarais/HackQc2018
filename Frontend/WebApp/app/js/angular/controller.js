var app = angular.module('appController', [])

app.controller('mainController', ['$scope', 'Request', function($scope, Request) {

    $scope.risques = [
        {
            show: false,
            title: "Inondations",
            icon: "icon_goutte.png"
        },
        {
            show: false,
            title: "Feu",
            icon: "icon_feu.png"
        }
    ]

    $scope.showHideRisque = function(i){
        $scope.risques[i].show = !$scope.risques[i].show;
        if($scope.risques[i].show){
            loadPins($scope.risques[i].title)
        }
        else{
            removeLayer($scope.risques[i].title)
        }
    }

    function loadPins(type){
        Request.get(type).then(function(data){
            pinsToMap(type, data.data.file);
        })
    }

    function pinsToMap(title, jsonFile){
        var image = new ol.style.Circle({
            radius: 5,
            fill: null,
            stroke: new ol.style.Stroke({color: 'red', width: 1})
        });

        var styles = {
            'Point': new ol.style.Style({
                image: image
            })
        };

        var styleFunction = function(feature) {
            return styles[feature.getGeometry().getType()];
        };

        var geojsonObject = jsonFile;

        var vectorSource = new ol.source.Vector({
            features: (new ol.format.GeoJSON()).readFeatures(geojsonObject,
                {featureProjection: map.getView().getProjection()}),

        });

        var vectorLayer = new ol.layer.Vector({
            title: title,
            source: vectorSource,
            style: styleFunction
        });
        map.addLayer(vectorLayer);
    }

    function removeLayer

}]);
