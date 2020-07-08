<template>
  <div>
      <v-card>
        <v-navigation-drawer
          permanent
          width="100%"
        >
          <v-list>
            <v-list-item link>
              <v-list-item-content>
                <v-list-item-title class="title">{{this.projectName}}</v-list-item-title>
              </v-list-item-content>
            </v-list-item>
          </v-list>

          <v-divider></v-divider>
                  
          <v-card>
            <v-card-text>Dodaj lokalizacje</v-card-text>

            <v-card-actions>
          
              <v-text-field
                v-model="locationName"
                label="Nowa lokalizacja"
                :rules="locationNameRules"
                required
              ></v-text-field>
              <v-btn icon color="green" @click="addLocation(locationName)" :disabled=isFilled><v-icon>fa-plus-circle</v-icon></v-btn>
            </v-card-actions>
            
            <v-divider></v-divider>
            <v-list
              nav
              dense
              style="max-height: 500px"
              class="overflow-y-auto"
            >
             
              <v-list-item v-for="word in wordsInProject" :key="word">
              {{word}}
                <v-layout align-end justify-end>
                  <v-btn icon @click="updateLocationForm(word)"><v-icon>fas fa-edit</v-icon></v-btn>
                  <v-btn icon color="red"  @click="removeLocation(word)"><v-icon>fa fa-trash</v-icon></v-btn>
                </v-layout>

              </v-list-item>
            </v-list>

          </v-card>                  
        </v-navigation-drawer>
      </v-card>

      <v-dialog  v-model="showDialogAddWord" width="50%" persistent>
          <v-card>
            <v-card-title>Analiza dodanego słowa</v-card-title>
            <v-divider></v-divider>
            <v-card-text style="height: 300px;">
              Jeśli słowo występuje w tekscie. Zostanie dodane do projektu i pojawi się w liście na dole.
                <v-row wrap justify="center">
                  <v-icon v-if="wordAdded" color="green">far fa-check-circle</v-icon>
                  <v-icon v-if="!wordAdded">fas fa-circle-notch fa-spin</v-icon>
                </v-row>
              
            </v-card-text>
            <v-divider></v-divider>
          </v-card>
      </v-dialog>

       <v-dialog  v-model="showDialogUpdateWord" width="50%">
          <v-card style="height: 300px;">
            <v-card-title>Zmień współrzędne dla lokalizacji: {{locationNameToChange}}</v-card-title>
            <v-divider></v-divider>
            <v-card-text>
              <v-form
                ref="form"
                v-model="valid"
                lazy-validation
              >
                <v-text-field
                  v-model="latitude "
                  :rules="rulesLatitude"
                  label="Szerokość geograficzna"
                  required
                ></v-text-field>

                <v-text-field
                  v-model="longitude"
                  :rules="rulesLongitude"
                  label="Długość geograficzna"
                  required
                ></v-text-field>

                <v-btn
                :disabled="!valid"
                color="success"
                class="mr-4"
                @click="updateLocation()"
                >
                  Zatwierdz
                </v-btn>
              </v-form>
               
            </v-card-text>
            <v-divider></v-divider>
          </v-card>
      </v-dialog>

  </div>
</template>

<script>
  export default {
    name : 'MapMenu',

    props: {
      projectName: String,
      locationSections: Array,
      
    },


    data () {
      return {
        wordsInProject: [],
        locationName: '',
        showDialogAddWord: false,
        wordAdded: false,
        

        showDialogUpdateWord: false,
        locationNameToChange: '',
        latitude: '',
        longitude: '',
        valid: false,

        rulesLatitude: [
          v => (v && v.length >= 4) || 'Musi zawierać 4 znaki',
          v => /^-?(90|[0-8]?\d)(\.\d+)/.test(v) || 'Zły format! Przykład: 24.525'
        ],

        rulesLongitude: [
          v => (v && v.length >= 4) || 'Musi zawierać 4 znaki',
          v => /-?(180|1[0-7]\d|\d?\d)(\.\d+)?$/.test(v) || 'Zły format! Przykład: 53.525'
        ],

        locationNameRules: [
          v => (v && v.length > 3) || 'Musi zawierać 4 znaki',

        ]


      }
    },

    mounted() {
      this.getAllLocations();
    },

    methods: {
      removeLocation(locationName) {
        this.$http.get('/api/project/delete?projectName=' + this.projectName + "&word=" + locationName, {
                        'Access-Control-Allow-Origin :' : '*'
                    }).then(function(response) {
                      console.log(response);
                      this.$router.go()
                    }, response => {      
                        alert("Coś poszło nie tak: " + response.bodyText)
                    })
      },

      addLocation(locationName) {
        this.showDialogAddWord = true;
        this.wordAdded = false;
        this.$http.get('/api/project/add?projectName=' + this.projectName + "&word=" + locationName, {
                        'Access-Control-Allow-Origin :' : '*'
                    }).then(function(response) {
                        console.log(response);
                        this.showDialogAddWord = false;
                        this.wordAdded = true;
                        this.$router.go()
                    }, response => {
                          alert("Coś poszło nie tak" + response.bodyText)
                    })
      },

      getAllLocations() {
        let tempArray = [];
        for (let i = 0; i < this.locationSections.length; i++) {
          for(let j = 0; j <this.locationSections[i].locations.length; j++) {
            tempArray.push(this.locationSections[i].locations[j].name)
          }
        }
        let uniqueSet = new Set(tempArray); 
        this.wordsInProject = [...uniqueSet]
      },

      updateLocationForm(locationName) {
        this.showDialogUpdateWord = true;
        this.locationNameToChange = locationName;
      },

      updateLocation() {
        let newCordinates = [this.latitude, this.longitude];

         this.$http.get('/api/project/modify?projectName=' + this.projectName + "&word=" + this.locationNameToChange + "&coordinates=" + newCordinates, {
                        'Access-Control-Allow-Origin :' : '*'
                    }).then(function() {
                      this.$router.go()
                    }, response => {
                      alert("Coś poszło nie tak " + response.bodyText)
                    })        
      }

    },

    computed: {
        isFilled () {
            if(this.locationName.length > 3)
                return false;
            else
                return true;
        }
    }


  }
</script>
