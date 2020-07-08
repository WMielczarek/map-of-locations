<template>
    <v-dialog :value="value" @input="$emit('input')" width="50%" persistent>
        <v-card>
        <v-card-title>Projekt o nazwie {{this.projectName}} nie jest gotowy.</v-card-title>
        <v-divider></v-divider>
        <v-card-text style="height: 300px;">
        
            <v-list subheader>
                <v-subheader>Projekt nie jest gotowy do użycia jeszcze, znajduje się na etapie: </v-subheader>
                <v-row>
                    <v-list-item>
                        <v-list-item-content>
                            <v-list-item-title v-text="currentProjectStatus"></v-list-item-title>
                        </v-list-item-content>
                    </v-list-item>
                </v-row>
            </v-list>
        </v-card-text>
        </v-card>
    </v-dialog>
</template>

<script>
export default {
    name: "DialogRequest",
    props: {
        projectName: String,
        value: Boolean,
        currentProjectStatus: String
    },

    mounted() {
        if(this.currentProjectStatus !== "GETTING_LOCATIONS_FROM_NOMINATIM" || this.currentProjectStatus !== "READY") {
            this.getLocations(this.projectName);
        }
    },

    methods: {

     
            getLocations(projectName) {
                 this.$http.get('/api/locations?projectName='+projectName, {
                        'Access-Control-Allow-Origin :' : '*'
                    }).then(function() {
                        this.$router.push({path: `/map/${this.projectId}`})
                    })
            }
    }
}
</script>