<template>
  <div ma-0 pa-0>
      <v-row>        
        <v-col md="9">
          <div class = "view-map">
              <div 
                id="map" 
                class="map">
              </div>
          </div>

          <v-col md="1">
            <v-slider v-if="projectDataLoaded"
              v-model="slider"
              class="slider"
              :max="maxOcurrences"
              :min=0
              hide-details
              step="1"
              ticks="always"
              tick-size="5"
              label="Rozdzielczość"
              vertical
              @change="changeSlider()"
              >
            </v-slider>
          </v-col>

          <v-col md="5">
            <v-range-slider v-if="projectDataLoaded"
            v-model="range"
            :max="sectionNumber"
            :min=0
            hide-details
            class="range-slider"
            step="1"
            ticks="always"
            tick-size="5"
            label="Rozdziały"
            @change="changeSlider()"
            >
            </v-range-slider>
          </v-col>

    
        </v-col>
        <v-col md="3">
          <mapMenu v-if="projectDataLoaded" v-bind:projectName="this.project.projectName" v-bind:locationSections="this.project.locationSections"/>
        </v-col>

      </v-row>
                
      <dialog-project-status v-model="showDialogProjectStaus" v-bind:projectName="this.projectName" v-bind:currentProjectStatus="projectStatus"/>
  </div>
</template>

<script >
import Collection from 'ol/Collection';
import 'ol/ol.css';
import Feature from 'ol/Feature';
import Map from 'ol/Map';
import View from 'ol/View';
import GeoJSON from 'ol/format/GeoJSON';
import {Tile as TileLayer, Vector as VectorLayer} from 'ol/layer';
import {OSM, Vector as VectorSource} from 'ol/source';
import {Circle as CircleStyle, Fill, Stroke, Style} from 'ol/style';

import DialogProjectStatus from './DialogProjectSatus';
import MapMenu from './MapMenu';


export default {
  name: "MainMap",
  components: { MapMenu, DialogProjectStatus},

  data: function() {
    return {

        project: [],
        sectionNumber: 0,

        showDialogProjectStaus: false,
        projectDataLoaded: false,
        projectStatus: '',
        projectName: '',
        maxOcurrences: 0,
        layer: null,
  
        slider: 0,
        range: [0, 2],
    }
  },
 

  mounted() {
    this.checkProjectStatus();
  },

  methods: {

    changeSlider() {
      this.layer.getSource().clear()
      console.log(this.project.locationSections.slice(this.range[0], this.range[1]));
      this.drawOnMap(this.project.locationSections.slice(this.range[0], this.range[1]), this.slider)
    },

    getMaxOcurrences(project) {
      project.locationSections.forEach(section => {
        section.locations.forEach(location => {
          if(location.occurrences > this.maxOcurrences)
            this.maxOcurrences = location.occurrences;
        })
      });
    },

    fillData(project) {
      this.sectionNumber = project.locationSections.length;
      this.getMaxOcurrences(project);
      this.project = project;
      this.range = [0, this.sectionNumber];
      this.projectDataLoaded = true;
      this.getMap();
      this.drawOnMap(project.locationSections, this.slider);
    },
    

    getProject(projectName) {
      this.$http.get('/api/project?projectName='+projectName, {
              'Access-Control-Allow-Origin :' : '*'
          }).then(function(response) {
            this.fillData(response.body);
          }, response => {
              if(response.status == 400) {
              alert("Takie projekt nie istnieje.")
          }
      })
    },

    checkProjectStatus() {
      this.projectName = this.$route.params.projectName;
      this.$http.get('/api/project/status?projectName='+this.projectName, {
              'Access-Control-Allow-Origin :' : '*'
          }).then(function(response) {
            if(response.body != "READY")
              this.showDialogProjectStaus = true;
            else 
              this.getProject(this.projectName)
            this.projectStatus = response.body;
            
          }, response => {
          //error
          if(response.status == 400) {
              alert("Takie projekt nie istnieje.")
          }
      })
    },

    drawOnMap(LocationSections, x) {
      var collection = new Collection();
    
       LocationSections.forEach(section => {
        section.locations.forEach(location => {
          if(location.position !== null && location.occurrences >= x) 
            collection.push(new GeoJSON().readFeature(location.position))
  
        })
      });

     
      for(var z = 0; z<collection.values_.length; z++){
          collection.array_[z].setId(z);
      }
      var feature1 = new Feature();
      feature1.setId('11')
      var image = new CircleStyle({
      radius: 15,
      fill: new Fill({
          color: 'rgba(0, 0, 255, 0.1)'}),
      stroke: new Stroke({color: 'purple', width: 1})
    });
    
    var styles = {
      'Point': new Style({
        image: image
      })
    }
          
      
    var styleFunction = function(feature) {
      return styles[feature.getGeometry().getType()];
    };

    var vectorSource = new VectorSource({
        features : collection
    })
      
    this.layer = new VectorLayer({
      source : vectorSource,
      style: styleFunction,
      map : this.map,
    })

  },

  getMap() {
      
  this.map = new Map({
    layers: [
      new TileLayer({
        source: new OSM()
      }),
      
    ],
    target: 'map',
    view: new View({
      center: [0, 0],
      projection : 'EPSG:4326',
      zoom: 2,
      
    })
  });   
  }   
      
}
}
</script>

<style>
    .map {
      
      top: 0px;
      height: 100%;
      width: 100%;
      background: transparent;
      
    }
    .view-map {
      position: absolute;
      width : 100%;
      height : 80%;
    }

    .range-slider {
      width: 100%;
      margin: 20em;



    }
     .slider {
      width: 100%;
      margin: 5em 0em 0em 0em;


    }
   
 </style>