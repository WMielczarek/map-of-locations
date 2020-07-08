import MainMap from './components/MainMap.vue';
import NewProject from './components/NewProject.vue';
import Home from './components/Home.vue';
import OpenProject from './components/OpenProject';
import About from './components/About';

export const routes = [
        {path : '/map/:projectName', component: MainMap },
        {path : '/map', component : MainMap},
        {path : '/newProject', component : NewProject},
        {path : '/openProject', component : OpenProject},
        {path : '/about', component: About},
        {path : '/', component : Home}

];