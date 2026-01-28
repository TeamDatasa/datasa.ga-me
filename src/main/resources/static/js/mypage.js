
function authHeaders() {
  const token = localStorage.getItem("accessToken");
  return {
    "Content-Type": "application/json",
    "Authorization": `Bearer ${token}`
  };
}

// Country mapping
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

// Load profile
async function loadProfile() {
  const token = localStorage.getItem("accessToken");
  if (!token) {
    alert("Login is required.");
    window.location.href = "/auth/login";
    return;
  }

  const res = await fetch("/api/mypage/profile", { headers: authHeaders() });

  if (res.status === 401 || res.status === 403) {
    alert("Session expired. Please log in again.");
    localStorage.removeItem("accessToken");
    window.location.href = "/auth/login";
    return;
  }

  if (!res.ok) {
    alert("Failed to load profile.");
    return;
  }

  const data = await res.json();

  // Top card
  document.getElementById("displayName").textContent = data.name ?? "-";
  document.getElementById("displayMeta").textContent =
    `${data.region ?? "-"} · ${data.age ?? "-"} yrs · ${countryCodeToName(data.countryCode) ?? "-"}`;
  document.getElementById("roleBadge").textContent = data.role ?? "-";
  document.getElementById("avatar").textContent = data.name ? data.name[0] : "?";

  // Form
  document.getElementById("name").value = data.name ?? "";
  document.getElementById("birthDate").value = data.birthDate ?? "";
  document.getElementById("gender").value = data.gender ?? "";

  // country input uses name (id="country")
  const countryEl = document.getElementById("country");
  if (countryEl) countryEl.value = countryCodeToName(data.countryCode);

  document.getElementById("region").value = data.region ?? "";
  document.getElementById("mbti").value = data.mbti ?? "";

  document.getElementById("smoking").value =
    data.smoking === true ? "true" : data.smoking === false ? "false" : "";

  document.getElementById("drinking").value =
    data.drinking === true ? "true" : data.drinking === false ? "false" : "";

  document.getElementById("bio").value = data.bio ?? "";

  // Role panels
  document.getElementById("hostPanel").style.display =
    data.role === "HOST" ? "block" : "none";
  document.getElementById("userPanel").style.display =
    data.role === "USER" ? "block" : "none";
}

// Save profile
document.getElementById("profileForm").addEventListener("submit", async (e) => {
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

  const res = await fetch("/api/mypage/profile", {
    method: "PUT",
    headers: authHeaders(),
    body: JSON.stringify(payload)
  });

  const hint = document.getElementById("saveHint");

  if (res.ok) {
    hint.textContent = "Profile saved successfully.";
    await loadProfile();
  } else {
    const text = await res.text().catch(() => "");
    hint.textContent = text || "Failed to save profile. Please check your input.";
  }
});

// Logout / Delete
document.getElementById("logoutBtn").addEventListener("click", () => {
  localStorage.removeItem("accessToken");
  localStorage.removeItem("userId");
  localStorage.removeItem("email");
  localStorage.removeItem("role");
  window.location.href = "/auth/login";
});

document.getElementById("deactivateBtn").addEventListener("click", async () => {
  const ok = confirm("Are you sure you want to delete your account?");
  if (!ok) return;

  const res = await fetch("/api/mypage", {
    method: "DELETE",
    headers: authHeaders()
  });

  if (!res.ok) {
    alert("Failed to delete account.");
    return;
  }

  alert("Your account has been deleted.");
  localStorage.removeItem("accessToken");
  localStorage.removeItem("userId");
  localStorage.removeItem("email");
  localStorage.removeItem("role");
  window.location.href = "/";
});

document.addEventListener("DOMContentLoaded", loadProfile);

document.getElementById("resetPwBtn")?.addEventListener("click", () => {
  const email = localStorage.getItem("email") || "";
  window.location.href = `/auth/forgot-password?email=${encodeURIComponent(email)}`;
});


(function initRolePills(){
    const current = document.getElementById('currentRole')?.value || 'USER';
    document.querySelectorAll('.role-pill').forEach(btn=>{
      btn.classList.toggle('is-active', btn.dataset.role === current);
    });
  })();

  async function setRole(role){
    try{
      const res = await fetch('/api/mypage/role', {
        method: 'PATCH',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ role })
      });

      if(!res.ok){
        const txt = await res.text();
        alert('Role change failed: ' + txt);
        return;
      }

      // UI 업데이트
      document.getElementById('currentRole').value = role;
      document.querySelectorAll('.role-pill').forEach(btn=>{
        btn.classList.toggle('is-active', btn.dataset.role === role);
      });


      alert('Role updated to ' + role);
    }catch(e){
      alert('Role change error');
      console.error(e);
    }
  }


