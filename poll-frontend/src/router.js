// src/router.js
import { createRouter, createWebHistory } from "vue-router";
import Login from "./pages/Login.vue";
import Home from "./pages/Home.vue";
import api from "./api/api.js";
import CreatePoll from "./pages/CreatePoll.vue";

const routes = [
    { path: "/", component: Login },
    {
        path: "/home",
        component: Home,
        beforeEnter: async (to, from, next) => {
            try {
                await api.get("/auth/me");
                next();
            } catch {
                next("/");
            }
        }
    },
    {
        path: "/create-poll",
        component: CreatePoll,
        beforeEnter: async (to, from, next) => {
            try {
                await api.get("/auth/me");
                next();
            } catch {
                next("/");
            }
        }
    },
    {
        path: "/admin",
        component: () => import("./pages/AdminPage.vue")
    },
    {
        path: "/my-polls",
        component: () => import("./pages/MyPolls.vue")
    }
];

export default createRouter({
    history: createWebHistory(),
    routes
});
