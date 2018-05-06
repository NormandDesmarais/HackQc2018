var app = angular.module('appController', [])

app.controller('mainController', ['$scope', 'Request', function($scope, Request) {

    var alertes = [];
    Request.getAll().then(function(data){
        for(i in data.data.alertes){
            data.data.alertes[i].alerte.risque = data.data.alertes[i].alerte.type;
            data.data.alertes[i].alerte.type = "Feature";
            var exist = false;
            for(j in alertes){
                if(data.data.alertes[i].alerte.risque != alertes[j].type){
                    continue;
                }
                else{
                    exist = true;
                    alertes[j].data.push(data.data.alertes[i].alerte);
                    break;
                }
            }
            if(!exist){
                alertes.push({
                    type: data.data.alertes[i].alerte.risque,
                    data: []
                });
                alertes[alertes.length - 1].data.push(data.data.alertes[i].alerte);
            }

        }
        console.log(alertes);
        loadAllPins(alertes);
    })

    $scope.risques = [
        {
            show: false,
            title: "Tout",
            icon: "icon_acclimate.png",
            date:{
                debut:"",
                fin:""
            }
        },
        {
            show: false,
            title: "Inondations",
            icon: "icon_goutte.png",
            date:{
                debut:"",
                fin:""
            }
        },
        {
            show: false,
            title: "Users",
            icon: "icon_feu.png"
        },
        {
            show: false,
            title: "Vent",
            icon: "icon_vent.png"
        },
        {
            show: false,
            title: "Séisme",
            icon: "icon_seisme.png"
        }
    ]

    $scope.showHideRisque = function(i){
        $scope.risques[i].show = !$scope.risques[i].show;
        if($scope.risques[i].show){

        }
        else{
            removeLayer($scope.risques[i].title)
        }
    }

    function loadAllPins(alertes){
        for(i in alertes){
            var jsonObject = {
                type: "FeatureCollection",
                features: alertes[i].data
            }
            pinsToMap(alertes[i].type, jsonObject);
        }
    }

    function loadPins(type){

    }


    function pinsToMap(type, jsonFile){
        var icon;

        switch (type){
            case "Inondation" :
                icon = "../images/pin_goutte.png";
                break;
            case "Suivis" :
                icon = "../images/pin_goutte.png";
                break;
        }

        var image = new ol.style.Icon(/** @type {olx.style.IconOptions} */ ({
            anchor: [0.5, 30],
            anchorXUnits: 'fraction',
            anchorYUnits: 'pixels',
            opacity: 0.8,
            src: icon
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
            title: type,
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



    $scope.search = function(){
        Request.googleAPI($scope.searchForm).then(function(data){
            var point = [data.data.results[0].geometry.location.lng, data.data.results[0].geometry.location.lat];
            var size = /** @type {ol.Size} */ (map.getSize());
            view.centerOn(ol.proj.transform(point, 'EPSG:4326', 'EPSG:3857'), size, [size[0]/2, size[1]/2]);
        })
    }

}]);
