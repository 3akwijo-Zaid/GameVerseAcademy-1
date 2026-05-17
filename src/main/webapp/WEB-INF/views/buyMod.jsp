<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="ma.ac.esi.gameverseacademy.model.Mod, ma.ac.esi.gameverseacademy.model.User" %>
<!DOCTYPE html>
<html lang="fr">
<head>
<meta charset="UTF-8"><meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>Acheter — GameVerse</title>
<link href="https://fonts.googleapis.com/css2?family=Rajdhani:wght@600;700&family=Inter:wght@400;500&display=swap" rel="stylesheet">
<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css">
<style>
*,*::before,*::after{box-sizing:border-box;margin:0;padding:0}
:root{--bg:#060810;--surface:#0e1117;--surface2:#151b26;--border:rgba(255,255,255,0.07);--accent:#00d4ff;--accent2:#7c3aed;--text:#e2e8f0;--muted:#64748b;--success:#10b981}
body{font-family:'Inter',sans-serif;background:var(--bg);color:var(--text);min-height:100vh;display:flex;align-items:center;justify-content:center;padding:40px 20px}
body::before{content:'';position:fixed;inset:0;background-image:linear-gradient(rgba(0,212,255,0.025) 1px,transparent 1px),linear-gradient(90deg,rgba(0,212,255,0.025) 1px,transparent 1px);background-size:60px 60px;pointer-events:none;z-index:0}
.container{position:relative;z-index:1;width:100%;max-width:520px;background:var(--surface);border:1px solid var(--border);border-radius:20px;padding:36px;box-shadow:0 40px 80px rgba(0,0,0,0.5)}
.header{display:flex;align-items:center;gap:12px;margin-bottom:28px}
.header .icon{width:44px;height:44px;border-radius:10px;background:linear-gradient(135deg,var(--success),#059669);display:flex;align-items:center;justify-content:center;font-size:18px;color:#fff}
.header h1{font-family:'Rajdhani',sans-serif;font-size:24px;font-weight:700}
.header p{font-size:12px;color:var(--muted);margin-top:2px}
.mod-preview{display:flex;align-items:center;gap:14px;background:var(--surface2);border:1px solid var(--border);border-radius:12px;padding:14px;margin-bottom:24px}
.mod-preview img{width:64px;height:64px;border-radius:8px;object-fit:cover}
.mod-preview .no-img{width:64px;height:64px;border-radius:8px;background:var(--bg);display:flex;align-items:center;justify-content:center;color:rgba(0,212,255,0.2);font-size:24px}
.mod-title{font-family:'Rajdhani',sans-serif;font-size:18px;font-weight:700}
.mod-price{font-size:20px;font-weight:700;color:var(--success);margin-top:4px}
.form-group{margin-bottom:18px}
label{display:block;font-size:11px;font-weight:600;color:var(--muted);text-transform:uppercase;letter-spacing:1px;margin-bottom:7px}
input{width:100%;padding:11px 14px;background:var(--surface2);border:1px solid var(--border);border-radius:10px;color:var(--text);font-size:14px;font-family:inherit;transition:border-color 0.2s}
input:focus{outline:none;border-color:var(--accent);box-shadow:0 0 0 3px rgba(0,212,255,0.1)}
.card-row{display:grid;grid-template-columns:2fr 1fr 1fr;gap:12px}
.btn-submit{width:100%;padding:14px;background:linear-gradient(135deg,var(--success),#059669);border:none;border-radius:10px;color:#fff;font-size:15px;font-weight:700;font-family:'Rajdhani',sans-serif;cursor:pointer;letter-spacing:1px;text-transform:uppercase;transition:opacity 0.2s,transform 0.2s;margin-top:8px}
.btn-submit:hover{opacity:0.9;transform:translateY(-1px)}
.back-link{display:inline-flex;align-items:center;gap:6px;color:var(--muted);font-size:13px;text-decoration:none;margin-top:16px;transition:color 0.2s}
.back-link:hover{color:var(--accent)}
.security-note{display:flex;align-items:center;gap:6px;font-size:11px;color:var(--muted);margin-top:12px;justify-content:center}
</style>
</head>
<body>
<% Mod mod = (Mod) request.getAttribute("mod"); String displayImg = mod != null ? mod.getDisplayImage() : null; %>
<div class="container">
  <div class="header"><div class="icon"><i class="fas fa-lock"></i></div><div><h1>Paiement Securise</h1><p>Transaction cryptee SSL</p></div></div>
  <% if (mod != null) { %>
  <div class="mod-preview">
    <% if (displayImg != null && !displayImg.isEmpty()) { %><img src="<%= displayImg.startsWith("http") ? displayImg : request.getContextPath() + "/" + displayImg %>" alt="<%= mod.getTitle() %>"><% } else { %><div class="no-img"><i class="fas fa-dice-d20"></i></div><% } %>
    <div><div class="mod-title"><%= mod.getTitle() %></div><div class="mod-price"><%= String.format("%.2f EUR", mod.getPrice()) %></div></div>
  </div>
  <form action="<%= request.getContextPath() %>/cards" method="POST">
    <input type="hidden" name="modId" value="<%= mod.getId() %>">
    <input type="hidden" name="price" value="<%= mod.getPrice() %>">
    <div class="form-group"><label>Titulaire de la carte</label><input type="text" name="cardHolder" placeholder="Jean Dupont" required></div>
    <div class="form-group"><label>Numero de carte</label><input type="text" name="cardNumber" placeholder="1234 5678 9012 3456" maxlength="19" oninput="formatCard(this)" required></div>
    <div class="card-row">
      <div class="form-group"><label>Date expiration</label><input type="text" name="expiry" placeholder="MM/AA" maxlength="5" required></div>
      <div class="form-group"><label>CVV</label><input type="text" name="cvv" placeholder="123" maxlength="3" required></div>
    </div>
    <button type="submit" class="btn-submit"><i class="fas fa-lock"></i> &nbsp;Payer <%= String.format("%.2f EUR", mod.getPrice()) %></button>
  </form>
  <div class="security-note"><i class="fas fa-shield-alt"></i> Paiement 100% securise — Visa, Mastercard acceptes</div>
  <% } %>
  <a href="<%= request.getContextPath() %>/mods" class="back-link"><i class="fas fa-arrow-left"></i> Retour</a>
</div>
<script>
function formatCard(input) {
  let v = input.value.replace(/\D/g,'').substring(0,16);
  input.value = v.replace(/(.{4})/g,'$1 ').trim();
}
</script>
</body>
</html>
