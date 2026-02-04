export async function authFetch(url, options = {}) {

    const headers = {
        ...(options.headers || {})
    };

    // JSON body일 때만 Content-Type 자동 세팅
    if (!headers["Content-Type"] && options.body && !(options.body instanceof FormData)) {
        headers["Content-Type"] = "application/json";
    }

    const response = await fetch(url, {
        ...options,
        headers,
        credentials: "include", // ⭐ 쿠키 JWT 핵심
    });

    // 🔥 인증 만료 / 실패 → 자동 로그아웃
    if (response.status === 401) {
        console.warn("AUTH EXPIRED:", url);

        try {
            await fetch("/api/auth/logout", {
                method: "POST",
                credentials: "include",
            });
        } catch (e) {}

        window.location.href = "/";
        throw new Error("UNAUTHORIZED");
    }

    return response;
}
