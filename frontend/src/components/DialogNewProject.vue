<template>
    <v-dialog :value="value" @input="$emit('input')" width="50%" persistent>
        <v-card>
        <v-card-title>Tworzenie projektu o nazwie: {{this.projectName}}</v-card-title>
        <v-divider></v-divider>
        <v-card-text style="height: 400px;">
           
            <v-list subheader>
                <v-subheader>Etapy tworzenia i analizy projektu. Moze potrwać do kilkudzesieciu minut.</v-subheader>
                <v-subheader><p style="color: red">Nie zamykaj karty przegladarki do zakonczenia etapu "Zakonczenie analizy tekstu"</p></v-subheader>
                <v-row>
                    <v-list-item
                        v-for="requestStep in requestSteps"
                        :key="requestStep.title"
                    >
                        <v-list-item-icon>
                            <v-icon v-if="requestStep.done" color="green">far fa-check-circle</v-icon>
                            <v-icon v-if="!requestStep.done">fas fa-circle-notch fa-spin</v-icon>
                        </v-list-item-icon>
                        <v-list-item-content>
                            <v-list-item-title v-text="requestStep.title"></v-list-item-title>
                        </v-list-item-content>
                    </v-list-item>
                </v-row>
            </v-list>
        </v-card-text>
        <v-divider></v-divider>
      </v-card>
    </v-dialog>
</template>

<script>
export default {
    name: "DialogNewProject",
    props: {
        projectName: String,
        value: Boolean,
        requestSteps: Array
    },
}
</script>