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
        var image = new ol.style.Icon(/** @type {olx.style.IconOptions} */ ({
            anchor: [0.5, 46],
            anchorXUnits: 'fraction',
            anchorYUnits: 'pixels',
            opacity: 0.8,
            src: '../images/pin_goutte.png'
          }))

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

    function removeLayer(type){
        var layersToRemove = [];
        map.getLayers().forEach(function (layer) {
            if (layer.get('title') != undefined && layer.get('title') === type) {
                layersToRemove.push(layer);
            }
        });

        var len = layersToRemove.length;
        for(var i = 0; i < len; i++) {
            map.removeLayer(layersToRemove[i]);
        }
    }

}]);
