<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List, ma.ac.esi.gameverseacademy.model.User, ma.ac.esi.gameverseacademy.model.GameCard" %>
<!DOCTYPE html>
<html lang="fr">
<head>
<meta charset="UTF-8"><meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>Mon Profil — GameVerse</title>
<link href="https://fonts.googleapis.com/css2?family=Rajdhani:wght@600;700&family=Inter:wght@400;500&display=swap" rel="stylesheet">
<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css">
<style>
*,*::before,*::after{box-sizing:border-box;margin:0;padding:0}
:root{--bg:#060810;--surface:#0e1117;--surface2:#151b26;--border:rgba(255,255,255,0.06);--accent:#00d4ff;--accent2:#7c3aed;--text:#e2e8f0;--muted:#64748b;--success:#10b981;--danger:#ef4444}
body{font-family:'Inter',sans-serif;background:var(--bg);color:var(--text);min-height:100vh;padding:40px}
body::before{content:'';position:fixed;inset:0;background-image:linear-gradient(rgba(0,212,255,0.02) 1px,transparent 1px),linear-gradient(90deg,rgba(0,212,255,0.02) 1px,transparent 1px);background-size:60px 60px;pointer-events:none}
.back-link{display:inline-flex;align-items:center;gap:6px;color:var(--muted);font-size:13px;text-decoration:none;margin-bottom:28px;padding:8px 14px;border:1px solid var(--border);border-radius:8px;transition:all 0.2s}
.back-link:hover{border-color:var(--accent);color:var(--accent)}
.layout{display:grid;grid-template-columns:320px 1fr;gap:24px;max-width:1100px}
.profile-card{background:var(--surface);border:1px solid var(--border);border-radius:16px;padding:28px;height:fit-content}
.avatar-big{width:80px;height:80px;border-radius:50%;background:linear-gradient(135deg,var(--accent),var(--accent2));display:flex;align-items:center;justify-content:center;font-family:'Rajdhani',sans-serif;font-size:32px;font-weight:700;color:#fff;margin:0 auto 16px}
.profile-name{font-family:'Rajdhani',sans-serif;font-size:22px;font-weight:700;text-align:center;margin-bottom:4px}
.profile-role{font-size:12px;color:var(--muted);text-align:center;margin-bottom:24px}
.profile-role.admin{color:var(--accent)}
.stat-row{display:flex;justify-content:space-between;padding:10px 0;border-bottom:1px solid var(--border);font-size:13px}
.stat-row:last-child{border-bottom:none}
.stat-row .label{color:var(--muted)}
.stat-row .val{font-weight:600;color:var(--accent)}
.panel{background:var(--surface);border:1px solid var(--border);border-radius:16px;padding:28px}
.panel-title{font-family:'Rajdhani',sans-serif;font-size:20px;font-weight:700;margin-bottom:20px;display:flex;align-items:center;gap:8px}
.panel-title i{color:var(--accent)}
.form-group{margin-bottom:16px}
label{display:block;font-size:11px;font-weight:600;color:var(--muted);text-transform:uppercase;letter-spacing:1px;margin-bottom:7px}
input{width:100%;padding:11px 14px;background:var(--surface2);border:1px solid var(--border);border-radius:10px;color:var(--text);font-size:14px;font-family:inherit;transition:border-color 0.2s}
input:focus{outline:none;border-color:var(--accent);box-shadow:0 0 0 3px rgba(0,212,255,0.1)}
.btn-submit{padding:11px 24px;background:linear-gradient(135deg,var(--accent),var(--accent2));border:none;border-radius:10px;color:#fff;font-size:14px;font-weight:700;font-family:'Rajdhani',sans-serif;cursor:pointer;letter-spacing:1px;text-transform:uppercase;transition:opacity 0.2s}
.btn-submit:hover{opacity:0.9}
.alert{padding:11px 14px;border-radius:9px;font-size:13px;margin-bottom:16px}
.alert-error{background:rgba(239,68,68,0.1);border:1px solid rgba(239,68,68,0.3);color:var(--danger)}
.alert-success{background:rgba(16,185,129,0.1);border:1px solid rgba(16,185,129,0.3);color:var(--success)}
.section-divider{border:none;border-top:1px solid var(--border);margin:24px 0}
.purchase-item{display:flex;align-items:center;justify-content:space-between;padding:10px 0;border-bottom:1px solid var(--border);font-size:13px}
.purchase-item:last-child{border-bottom:none}
.purchase-game{font-weight:500}
.purchase-price{color:var(--success);font-weight:600}
.purchase-date{color:var(--muted);font-size:11px}
.empty-purchases{text-align:center;padding:30px;color:var(--muted)}
.empty-purchases i{font-size:32px;display:block;margin-bottom:10px;color:rgba(0,212,255,0.15)}
</style>
</head>
<body>
<% User user = (User) session.getAttribute("user");
   List<GameCard> myCards = (List<GameCard>) request.getAttribute("myCards");
   String avatarLetter = user != null && user.getLogin().length() > 0 ? String.valueOf(user.getLogin().charAt(0)).toUpperCase() : "G";
   boolean isAdmin = user != null && "ADMIN".equals(user.getRole());
%>
<a href="<%= request.getContextPath() %>/mods" class="back-link"><i class="fas fa-arrow-left"></i> Retour</a>
<div class="layout">
  <div class="profile-card">
    <div class="avatar-big"><%= avatarLetter %></div>
    <div class="profile-name"><%= user != null ? user.getLogin() : "" %></div>
    <div class="profile-role <%= isAdmin ? "admin" : "" %>"><i class="fas fa-<%= isAdmin ? "crown" : "user" %>"></i> <%= isAdmin ? "Administrateur" : "Utilisateur" %></div>
    <div class="stat-row"><span class="label">Jeux achetes</span><span class="val"><%= myCards != null ? myCards.size() : 0 %></span></div>
    <div class="stat-row"><span class="label">Total depense</span><span class="val"><%= myCards != null ? String.format("%.2f EUR", myCards.stream().mapToDouble(c -> c.getPrice()).sum()) : "0.00 EUR" %></span></div>
  </div>
  <div>
    <div class="panel" style="margin-bottom:20px">
      <div class="panel-title"><i class="fas fa-lock"></i> Changer le mot de passe</div>
      <% if (request.getAttribute("error") != null) { %><div class="alert alert-error"><i class="fas fa-exclamation-circle"></i> <%= request.getAttribute("error") %></div><% } %>
      <% if (request.getAttribute("success") != null) { %><div class="alert alert-success"><i class="fas fa-check-circle"></i> <%= request.getAttribute("success") %></div><% } %>
      <form action="<%= request.getContextPath() %>/profile" method="POST">
        <div class="form-group"><label>Mot de passe actuel</label><input type="password" name="currentPassword" required></div>
        <div class="form-group"><label>Nouveau mot de passe</label><input type="password" name="newPassword" required></div>
        <div class="form-group"><label>Confirmer</label><input type="password" name="confirmPassword" required></div>
        <button type="submit" class="btn-submit"><i class="fas fa-save"></i> Sauvegarder</button>
      </form>
    </div>
    <div class="panel">
      <div class="panel-title"><i class="fas fa-shopping-bag"></i> Mes Achats (<%= myCards != null ? myCards.size() : 0 %>)</div>
      <% if (myCards != null && !myCards.isEmpty()) { for (GameCard gc : myCards) { %>
      <div class="purchase-item">
        <div><div class="purchase-game"><%= gc.getModTitle() %></div><div class="purchase-date"><%= gc.getPurchasedAt() != null ? gc.getPurchasedAt().toString().substring(0,10) : "" %> &bull; **** <%= gc.getCardNumberLast4() %></div></div>
        <div style="display:flex;align-items:center;gap:10px">
          <span class="purchase-price"><%= String.format("%.2f EUR", gc.getPrice()) %></span>
          <a href="<%= request.getContextPath() %>/cards?action=pdf&cardId=<%= gc.getId() %>" style="color:var(--accent);font-size:12px;text-decoration:none"><i class="fas fa-file-pdf"></i> Recu</a>
        </div>
      </div>
      <% } } else { %>
      <div class="empty-purchases"><i class="fas fa-shopping-bag"></i><p>Aucun achat pour le moment.</p></div>
      <% } %>
    </div>
  </div>
</div>
</body>
</html>
