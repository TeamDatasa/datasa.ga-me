function authHeaders() {
  return { "Content-Type": "application/json" };
}

// =============================
// Country mapping
// =============================
function countryNameToCode(name) {
  if (!name) return null;
  const n = name.trim().toLowerCase();
  const map = {
    "korea": "KR",
    "south korea": "KR",
    "republic of korea": "KR",
    "japan": "JP",
    "china": "CN",
    "usa": "US",
    "united states": "US"
  };
  return map[n] || null;
}

function countryCodeToName(code) {
  const map = { KR: "Korea", JP: "Japan", CN: "China", US: "USA" };
  return map[code] || code || "";
}

// =============================
// UI helpers
// =============================
function setRoleUI(role) {
  const currentRoleEl = document.getElementById("currentRole");
  if (currentRoleEl) currentRoleEl.value = role;

  document.querySelectorAll(".role-pill").forEach((btn) => {
    btn.classList.toggle("is-active", btn.dataset.role === role);
  });

  const hostPanel = document.getElementById("hostPanel");
  const userPanel = document.getElementById("userPanel");
  if (hostPanel) hostPanel.style.display = role === "HOST" ? "block" : "none";
  if (userPanel) userPanel.style.display = role === "USER" ? "block" : "none";

  const roleBadge = document.getElementById("roleBadge");
  if (roleBadge) roleBadge.textContent = role ?? "-";
}

function setHint(msg = "") {
  const hint = document.getElementById("saveHint");
  if (hint) hint.textContent = msg;
}

// =============================
// Load profile
// =============================
async function loadProfile() {
  const res = await authFetch("/api/mypage/profile", { method: "GET" });
  if (!res) return; // 401이면 authFetch가 리다이렉트 처리했을 수 있음

  if (!res.ok) {
    alert("Failed to load profile.");
    return;
  }

  const data = await res.json();

  // ===== Top card =====
  document.getElementById("displayName").textContent = data.name ?? "-";
  document.getElementById("displayMeta").textContent =
    `${data.region ?? "-"} · ${data.age ?? "-"} yrs · ${countryCodeToName(data.countryCode) ?? "-"}`;
  document.getElementById("avatar").textContent = data.name ? data.name[0] : "?";

  // ===== Form values =====
  document.getElementById("name").value = data.name ?? "";
  document.getElementById("birthDate").value = data.birthDate ?? "";
  document.getElementById("gender").value = data.gender ?? "";

  const countryEl = document.getElementById("country");
  if (countryEl) countryEl.value = countryCodeToName(data.countryCode);

  document.getElementById("region").value = data.region ?? "";
  document.getElementById("mbti").value = data.mbti ?? "";

  document.getElementById("smoking").value =
    data.smoking === true ? "true" : data.smoking === false ? "false" : "";

  document.getElementById("drinking").value =
    data.drinking === true ? "true" : data.drinking === false ? "false" : "";

  document.getElementById("bio").value = data.bio ?? "";

  // ===== Role UI (pills/panels/badge) =====
  setRoleUI(data.role ?? "USER");
}

// =============================
// Save profile (ONLY on Save button)
// =============================
function bindProfileSave() {
  const form = document.getElementById("profileForm");
  if (!form) return;

  form.addEventListener("submit", async (e) => {
    e.preventDefault();

    const payload = {
      name: document.getElementById("name").value.trim(),
      birthDate: document.getElementById("birthDate").value,
      gender: document.getElementById("gender").value,

      // country name -> code
      countryCode: countryNameToCode(document.getElementById("country")?.value),

      region: document.getElementById("region").value.trim(),
      mbti: document.getElementById("mbti").value.trim() || null,

      smoking: document.getElementById("smoking").value === "" ? null :
        document.getElementById("smoking").value === "true",

      drinking: document.getElementById("drinking").value === "" ? null :
        document.getElementById("drinking").value === "true",

      bio: document.getElementById("bio").value.trim() || null
    };

    const res = await authFetch("/api/mypage/profile", {
      method: "PUT",
      body: JSON.stringify(payload)
    });
    if (!res) return;

    if (res.ok) {
      setHint("Profile saved successfully.");
      await loadProfile();
    } else {
      const text = await res.text().catch(() => "");
      setHint(text || "Failed to save profile. Please check your input.");
    }
  });
}

// =============================
// Role change (IMMEDIATE save)
// =============================
async function setRole(role) {
  // UI 먼저 반영 (즉시 체감)
  setRoleUI(role);

  // ✅ 쿠키 기반 authFetch를 쓰는 게 핵심
  const res = await authFetch("/api/mypage/role", {
    method: "PATCH",
    body: JSON.stringify({ role })
  });
  if (!res) return;

  if (!res.ok) {
    const text = await res.text().catch(() => "");
    alert(text || "Failed to update role. Please try again.");
    await loadProfile(); // 서버 값으로 되돌리기
    return;
  }

  // ✅ 여기서 절대 이동하지 말기
  setHint("Role updated.");

  // ✅ 서버에서 저장된 role 다시 받아와서 UI 확정
  await loadProfile();
}

window.setRole = setRole;


// =============================
// Logout / Delete
// =============================
function bindAccountActions() {
  document.getElementById("logoutBtn")?.addEventListener("click", async () => {
    // ✅ 1) 프론트 토큰/정보 삭제 (이게 핵심)
    localStorage.removeItem("accessToken");
    localStorage.removeItem("userId");
    localStorage.removeItem("email");
    localStorage.removeItem("role");
    sessionStorage.removeItem("justLoggedIn");

    // ✅ 2) (선택) 서버 로그아웃도 시도 - 실패해도 무시
    try {
      await fetch("/logout", { method: "POST", credentials: "same-origin" });
    } catch (e) {
      // ignore
    }

    // ✅ 3) 비로그인 메인으로 이동
    window.location.href = "/";
  });


  document.getElementById("deactivateBtn")?.addEventListener("click", async () => {
    const ok = confirm("Are you sure you want to delete your account?");
    if (!ok) return;

    const res = await authFetch("/api/mypage", { method: "DELETE" });
    if (!res) return;

    if (!res.ok) {
      alert("Failed to delete account.");
      return;
    }

    alert("Your account has been deleted.");
    window.location.href = "/";
  });

  // resetPwBtn이 실제 DOM에 없을 수도 있으니 optional
  document.getElementById("resetPwBtn")?.addEventListener("click", () => {
    window.location.href = "/auth/forgot-password";
  });
}

// =============================
// Init
// =============================
document.addEventListener("DOMContentLoaded", async () => {
  bindProfileSave();
  bindAccountActions();

  // 초기 UI는 loadProfile이 서버 값으로 덮어씀
  setRoleUI(document.getElementById("currentRole")?.value || "USER");

  await loadProfile();
});
