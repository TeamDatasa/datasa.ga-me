import { authFetch } from "/js/authFetch.js";

export async function apiGet(url) {
  const res = await authFetch(url, { method: "GET" });
  if (!res.ok) throw new Error(await res.text());
  return res.headers.get("content-type")?.includes("application/json")
    ? res.json()
    : res.text();
}

export async function apiPost(url, body) {
  const res = await authFetch(url, {
    method: "POST",
    body: body instanceof FormData ? body : JSON.stringify(body),
  });
  if (!res.ok) throw new Error(await res.text());
  return res.headers.get("content-type")?.includes("application/json")
    ? res.json()
    : res.text();
}

export async function apiDelete(url) {
  const res = await authFetch(url, { method: "DELETE" });
  if (!res.ok) throw new Error(await res.text());
  return true;
}
