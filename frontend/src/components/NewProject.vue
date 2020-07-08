<template>
    <div>
        <v-container>
            <v-row>
                <v-col  md="6"
                        offset-md="3"
                >
                    <v-card>
                        <v-img
                                class="white--text align-end"
                                src="../assets/task.png"
                                max-height="300"
                        >
                        </v-img>

                        <v-card-text class="text--primary">
                            <div>
                                <input type="file" id ='file' ref="file" @change="handleFileUpload()"/>
                            </div>

                            <div>Wybierz korpus tekstowy do analizy (format zip)</div>
                        </v-card-text>
                        <v-col
                            cols="12"
                            md="10"
                        >
                            <v-text-field
                                v-model="projectId"
                                label="Indentyfikator projektu"
                                :rules="rulesName"
                                required

                            ></v-text-field>
                        </v-col>

                        <v-card-actions>
                            <v-btn id='btnNewProject' @click="submitFile()" :disabled=isFilled>Stwórz projekt</v-btn>
                        </v-card-actions>
                    </v-card>
                </v-col>
            </v-row>
        </v-container>
        <dialog-new-project v-model="showDialogWindow" v-bind:projectName="projectId" v-bind:requestSteps="requestSteps"/>
    </div>
</template>


<script>
import DialogNewProject from './DialogNewProject';

    export default {
        name: "NewProject",
        components : { DialogNewProject},

        data : function() {
            return {
                processing: true,
                user_mail : 'demo2019@nlpday.pl',
                file_id : '',
                status : '',
                counter : 0,
                projectId : '',
                file: '',
                fileUploaded: false,
                showDialogWindow: false,


                requestSteps: [
                    {
                        title: "Upload pliku do serwisu clarin",
                        done: false
                    },
                     {
                        title: "Rozpoczecie analizy tekstu w serwisie clarin",
                        done: false
                    },
                    {
                        title: "Zakonczenie analizy tekstu",
                        done: false
                    },
                    {
                        title: "Zapisanie analizy w bazie danych",
                        done: false
                    },
                    {
                        title: "Pobranie lokalizacji z serwisu nominatim.openstreetmap.org",
                        done: false
                    }
                ],

                rulesName: [
                v => (v && v.length > 5) || 'Nazwa musi zawierać conajmniej 6 znaków',
                ]

            }
        },
        methods : {

            handleFileUpload() {
                this.file = this.$refs.file.files[0];
                this.fileUploaded = true;
            },

            submitFile() {
                this.requestSteps.forEach(requestStep => requestStep.done = false);
                this.showDialogWindow = true;
                let formData = new FormData();
                formData.append('file', this.file);
                
                this.$http.post('/nlprest2/base/upload', formData, {
                }).then(function(response){
                    this.requestSteps[0].done=true;
                    this.startAnalyze(response.bodyText);
                })
                
            },

            startAnalyze(fileHandler) {
                
                 this.$http.get('/api/startAnalyze?fileHandler='+fileHandler+'&projectName='+this.projectId,{
                        'file_handler' : fileHandler,
                        'Access-Control-Allow-Origin :' : '*'
                    }).then(function(response){
                        this.requestSteps[1].done=true;
                        this.checkStatus(response.bodyText);
                        },response => {
                            alert("Coś poszło nie tak: " + response.bodyText);
                    })
                    
            },


            checkStatus(processId) {
                this.$http.get('/api/checkStatus/'+processId, {
                        'Access-Control-Allow-Origin :' : '*'
                    }).then(function(response) {
                        if(response.body.status != 'DONE')
                            this.checkStatus(processId);
                        else {
                            this.saveProject(response.body.value[0].fileID.substring(18));
                            this.requestSteps[2].done=true;
                        }
                    },response => {
                        alert("Coś poszło nie tak: " + response.bodyText);
                   
                })

            },

            saveProject(fileHandler) {
                    this.$http.get('/api/result?fileHandler='+fileHandler+'&projectName='+this.projectId, {
                        'Access-Control-Allow-Origin :' : '*'
                    }).then(function() {
                        this.requestSteps[3].done=true;
                        this.getLocations();
                    },response => {
                        alert("Coś poszło nie tak: " + response.bodyText);
                    })
            },

            getLocations() {
                 this.$http.get('/api/locations?projectName='+this.projectId, {
                        'Access-Control-Allow-Origin :' : '*'
                    }).then(function() {
                        this.requestSteps[4].done=true;
                        this.$router.push({path: `/map/${this.projectId}`})
                    },response => {
                        alert("Coś poszło nie tak: " + response.bodyText);
                    })
            }

        },

        computed: {
            isFilled () {
                if(this.projectId.length > 5 && this.fileUploaded)
                    return false;
                else
                    return true;
            }
        }

    }

</script>

