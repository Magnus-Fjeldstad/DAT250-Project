// HTTP helper that includes credentials so JSESSIONID is sent/received
const BASE_URL = "http://localhost:8080"; // adjust if needed

async function request(path: string, options: RequestInit = {}) {
    const response = await fetch(BASE_URL + path, {
        credentials: "include", // critical for session cookies
        headers: { "Content-Type": "application/json" },
        ...options,
    });

    let body: any = null;
    const txt = await response.text();
    try { body = txt ? JSON.parse(txt) : null; } catch { body = txt || null; }

    if (!response.ok && response.status !== 204) {
        throw new Error(`HTTP ${response.status}: ${typeof body === "string" ? body : JSON.stringify(body)}`);
    }
    return body as any;
}

export const http = {
    get: <T>(path: string): Promise<T> => request(path, { method: "GET" }),
    post: <T>(path: string, body: any): Promise<T> =>
        request(path, { method: "POST", body: JSON.stringify(body) }),
    put:  <T>(path: string, body: any): Promise<T> =>
        request(path, { method: "PUT", body: JSON.stringify(body) }),
    delete: (path: string): Promise<void> => request(path, { method: "DELETE" }),
};
