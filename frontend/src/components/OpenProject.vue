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
                            <v-btn id='btnNewProject' @click="openProject()" :disabled=isFilled>Otwórz projekt</v-btn>
                        </v-card-actions>
                    </v-card>
                </v-col>
            </v-row>
        </v-container>
    </div>
</template>

<script>
    export default {
        name: "OpenProject",


        data: () => ({
            projectId: '',
            
            rulesName: [
            v => (v && v.length > 5) || 'Nazwa musi zawierać conajmniej 6 znaków',
            ]
            
        }),

        methods: {
            openProject() {
                this.$http.get('/api/project/?projectName='+this.projectId, {
                        'Access-Control-Allow-Origin :' : '*'
                    }).then(function() {
                        this.$router.push({path: `/map/${this.projectId}`})
                    }, response => {
                        if(response.status == 400) {
                            alert("Taki projekt nie istnieje")
                    }
                })
            
            },
        },

        computed: {
            isFilled () {
                if(this.projectId.length > 5)
                    return false;
                else
                    return true;
            }
        }
}
</script>

<style scoped>

</style>