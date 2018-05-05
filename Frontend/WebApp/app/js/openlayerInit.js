var map = new ol.Map({
    layers: [
        new ol.layer.Tile({
            source: new ol.source.OSM()
        })
    ],
    target: 'map',
    controls: ol.control.defaults({
        attributionOptions: {
            collapsible: false
        }
    }),
    view: new ol.View({
        center: ol.proj.transform([-73.656830, 45.516136], 'EPSG:4326', 'EPSG:3857'),
        projection: 'EPSG:3857',
        zoom: 9
    })
});
