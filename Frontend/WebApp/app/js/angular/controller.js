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
        loadLeftPaneMenu(alertes);
    })

    function loadLeftPaneMenu(alertes){
        $scope.risques = [
            {
                show: true,
                title: "Tout",
                icon: "icon_acclimate.png",
                date:{
                    debut:"",
                    fin:""
                }
            }
        ]
        for(i in alertes){
            switch (alertes[i].type){
                case "Inondation" :
                    icon = "../images/icon_goutte.png";
                    break;
                case "Suivi des cours d'eau" :
                    icon = "../images/icon_goutte.png";
                    break;
                case "Vent" :
                    icon = "../images/icon_vent.png";
                    break;
                case "Pluie" :
                    icon = "../images/icon_pluie.png";
                    break;
            }
            $scope.risques.push({
                show: true,
                title: alertes[i].type,
                icon: icon,
                date:{
                    debut: "",
                    fin: ""
                }
            })
        }
    }

    $scope.showHideRisque = function(i){
        $scope.risques[i].show = !$scope.risques[i].show;
        if($scope.risques[i].show){
            if(i == 0){
                for(j in $scope.risques){
                    if(j == 0) continue;
                    if(!$scope.risques[j].show){
                        loadPins(alertes[j-1]);
                        $scope.risques[j].show = true;
                    }
                }
                return;
            }
            loadPins(alertes[i-1]);
            for(var i = 1; i < $scope.risques.length; i++){
                if($scope.risques[i].show == false){
                    return;
                }
            }
            $scope.risques[0].show = true;
        }
        else{
            $scope.risques[0].show = false;
            if(i == 0){
                for(j in $scope.risques){
                    if(j == 0) continue;
                    if($scope.risques[j].show){
                        removeLayer($scope.risques[j].title)
                        $scope.risques[j].show = false;
                    }
                }
                return;
            }
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

    function loadPins(alertes){
        var jsonObject = {
            type: "FeatureCollection",
            features: alertes.data
        }
        pinsToMap(alertes.type, jsonObject);
    }


    function pinsToMap(type, jsonFile){
        var icon = "../images/pin_feu.png";

        switch (type){
            case "Inondation" :
                icon = "../images/pin_goutte.png";
                break;
            case "Suivi des cours d'eau" :
                icon = "../images/pin_vent.png";
                break;
            case "Vent" :
                icon = "../images/pin_feu.png";
                break;
            case "Pluie" :
                icon = "../images/pin_feu.png";
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

    clickedPin = function(coords){
        console.log(coords);
        console.log(alertes);
        for(i in alertes){
            var found = false;
            for(j in alertes[i].data){
                if(Math.abs(alertes[i].data[j].geometry.coordinates[0] - coords[0]) < 0.00005 && Math.abs(alertes[i].data[j].geometry.coordinates[1] - coords[1]) < 0.0005){
                    overlay.getElement().innerHTML = "<h5>" +
                        alertes[i].data[j].risque +
                        "</h5><div>" +
                        alertes[i].data[j].description +
                        "</div><small>" +
                        alertes[i].data[j].source +
                        "</small><br><small>" +
                        alertes[i].data[j].dateDeMiseAJour +
                        "</small>";
                    return;
                }
            }
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
