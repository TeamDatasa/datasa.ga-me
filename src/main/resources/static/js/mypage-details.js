document.addEventListener("DOMContentLoaded", () => {
  const role = localStorage.getItem("role");
  const el = document.getElementById("detailsRoleHint");
  if (el) el.textContent = role ? `Current role: ${role}` : "Role unknown.";
});
