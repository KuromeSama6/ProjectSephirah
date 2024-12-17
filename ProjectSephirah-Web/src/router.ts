import { createRouter, createWebHashHistory, RouteRecordRaw } from "vue-router";
import HomePage from "./routes/HomePage.vue";
import NotFound from "./routes/error/NotFound.vue";
import WelcomPage from "./routes/WelcomPage.vue";
import Cookies from "js-cookie";
import SearchPage from "./routes/SearchPage.vue";
import MangaDetailsPage from "./routes/manga/MangaDetailsPage.vue";
import MangaReaderPage from "./routes/manga/MangaReaderPage.vue";
import CredentialsManagementPage from "./routes/CredentialsManagementPage.vue";

const routes: RouteRecordRaw[] = [
    {
        path: "/",
        component: HomePage,
    },
    {
        path: "/welcome",
        component: WelcomPage,
    },
    {
        path: "/search",
        component: SearchPage,
    },
    {
        path: "/:provider/manga/:mangaId/details",
        component: MangaDetailsPage,
    },
    {
        path: "/:provider/manga/:mangaId/read/:chapterId",
        component: MangaReaderPage,
        name: "MangaReader",
    },
    {
        path: "/credentials",
        component: CredentialsManagementPage,
    },

    { path: "/:pathMatch(.*)*", name: "NotFound", component: NotFound },
];

const router = createRouter({
    history: createWebHashHistory(), // enables hash-based navigation
    routes,
    scrollBehavior(to, from, savedPosition) {
        if (savedPosition) {
            return savedPosition; // For saved scroll positions like browser back/forward buttons
        } else if (to.hash) {
            // Check if the route has a hash (anchor)
            return {
                el: to.hash, // Scroll to the element matching the hash
                behavior: "smooth", // Optional: smooth scrolling
            };
        } else {
            return { top: 0 }; // Default scroll to top
        }
    },
});

router.beforeEach(async (to, from) => {


});

export default router;
