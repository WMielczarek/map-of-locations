<template>
    <div>
        <v-app id="inspire">
            <v-container fluid>
            <v-row>
                <v-col cols="12">
                <v-row
                    align="center"
                    justify="center"
                    class="white lighten-5"
                    style="height: 500px;"
                >
                    <v-card
                        class="mx-auto"
                        max-width="400"
                    >
                        <v-img
                        class="white--text align-end"
                        height="200px"
                        src="../assets/books-768125.jpg"
                        >
                        <v-card-title>Stwórz nowy projekt</v-card-title>
                        </v-img>

                        <v-card-text class="text--primary">
                        <div>
                            Tworzenie nowe projektu, dla korpusu tekstowego.
                        </div>

                        <div></div>
                        </v-card-text>

                        

                        <v-card-actions>
                            <router-link tag="v-btn" to="newProject">Stwórz projekt</router-link>
                        </v-card-actions>
                    </v-card>

                    <v-card
                        class="mx-auto"
                        max-width="400"
                        >
                        <v-img
                        class="white--text align-end"
                        height="200px"
                        src="../assets/close-up-photo-of-assorted-color-of-push-pins-on-map-1078850.jpg"
                        >
                        <v-card-title>Otwórz istniejący projekt</v-card-title>
                        </v-img>

                        <v-card-text class="text--primary">
                        <div>Wymagana nazwy istniejącego projektu.</div>
                        </v-card-text>
                    
                        <v-card-actions>
                            <router-link tag="v-btn"  id="button" to="openProject">Otwórz projekt</router-link>
                        </v-card-actions>
                    </v-card>
                </v-row>
                </v-col>
            </v-row>
            </v-container>
        </v-app>
    </div>
</template>

<script>
    export default {
        name: "Home",
        
        data : function() {
        return {
                processing: false,
                user_mail : 'demo2019@nlpday.pl',
                file_id : '',
                status : ''
            
        }
    },
        
        mounted() {
        
        },
        methods : {
            hello() {
                this.processing = ! this.processing;
            }
        }
    }
</script>

<style scoped>

</style>