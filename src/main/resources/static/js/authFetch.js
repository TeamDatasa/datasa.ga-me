/**
 * 인증이 필요한 API 호출용 공통 fetch 래퍼
 * - Authorization: Bearer <token>
 * - credentials: include (쿠키 인증도 지원)
 */
async function authFetch(url, options = {}) {
    const token = localStorage.getItem("accessToken");

    const headers = {
        ...(options.headers || {})
    };

    // JSON body 기본 헤더
    if (!headers["Content-Type"] && !(options.body instanceof FormData)) {
        headers["Content-Type"] = "application/json";
    }

    // JWT 헤더 자동 추가
    if (token && token.trim()) {
        headers["Authorization"] = `Bearer ${token}`;
    }

    const response = await fetch(url, {
        ...options,
        headers,
        credentials: "include"
    });

    // 인증 실패 로그 (디버깅용)
    if (response.status === 401 || response.status === 403) {
        console.warn("AUTH FAIL:", response.status, url);
    }

    return response;
}
