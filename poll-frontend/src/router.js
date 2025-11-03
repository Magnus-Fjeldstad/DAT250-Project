// src/router.js
import { createRouter, createWebHistory } from "vue-router";
import Login from "./pages/Login.vue";
import Home from "./pages/Home.vue";
import api from "./api";

const routes = [
    { path: "/", component: Login },
    {
        path: "/home",
        component: Home,
        beforeEnter: async (to, from, next) => {
            try {
                await api.get("/auth/me"); // checks session
                next();
            } catch {
                next("/");
            }
        }
    }
];

export default createRouter({
    history: createWebHistory(),
    routes
});
