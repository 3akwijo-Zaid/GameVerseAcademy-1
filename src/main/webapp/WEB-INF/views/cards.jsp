<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List, ma.ac.esi.gameverseacademy.model.GameCard, ma.ac.esi.gameverseacademy.model.User" %>
<!DOCTYPE html>
<html lang="fr">
<head>
<meta charset="UTF-8"><meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>Mes Achats — GameVerse</title>
<link href="https://fonts.googleapis.com/css2?family=Rajdhani:wght@600;700&family=Inter:wght@400;500&display=swap" rel="stylesheet">
<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css">
<style>
*,*::before,*::after{box-sizing:border-box;margin:0;padding:0}
:root{--bg:#060810;--surface:#0e1117;--surface2:#151b26;--border:rgba(255,255,255,0.06);--accent:#00d4ff;--text:#e2e8f0;--muted:#64748b;--success:#10b981;--danger:#ef4444}
body{font-family:'Inter',sans-serif;background:var(--bg);color:var(--text);min-height:100vh;padding:40px}
.page-title{font-family:'Rajdhani',sans-serif;font-size:32px;font-weight:700;margin-bottom:8px}
.page-title span{color:var(--accent)}
.subtitle{color:var(--muted);font-size:14px;margin-bottom:32px}
.back-link{display:inline-flex;align-items:center;gap:6px;color:var(--muted);font-size:13px;text-decoration:none;margin-bottom:24px;transition:color 0.2s}
.back-link:hover{color:var(--accent)}
.cards-grid{display:grid;grid-template-columns:repeat(auto-fill,minmax(320px,1fr));gap:20px;max-width:1200px}
.card-item{background:var(--surface);border:1px solid var(--border);border-radius:14px;padding:20px;transition:border-color 0.2s}
.card-item:hover{border-color:rgba(0,212,255,0.3)}
.card-header{display:flex;align-items:center;justify-content:space-between;margin-bottom:16px}
.card-game{font-family:'Rajdhani',sans-serif;font-size:18px;font-weight:700}
.card-status{padding:3px 10px;border-radius:20px;font-size:11px;font-weight:600;background:rgba(16,185,129,0.15);color:var(--success);border:1px solid rgba(16,185,129,0.3)}
.card-info{display:grid;grid-template-columns:1fr 1fr;gap:8px;margin-bottom:16px}
.info-item{background:var(--surface2);border-radius:8px;padding:10px}
.info-label{font-size:10px;color:var(--muted);text-transform:uppercase;letter-spacing:1px;margin-bottom:3px}
.info-val{font-size:13px;font-weight:500}
.card-price{font-family:'Rajdhani',sans-serif;font-size:20px;font-weight:700;color:var(--success)}
.card-actions{display:flex;gap:8px;margin-top:14px}
.btn-pdf{display:flex;align-items:center;gap:6px;padding:8px 16px;border-radius:8px;font-size:12px;font-weight:600;text-decoration:none;background:rgba(0,212,255,0.1);border:1px solid rgba(0,212,255,0.3);color:var(--accent);transition:all 0.2s}
.btn-pdf:hover{background:var(--accent);color:#000}
.btn-del{display:flex;align-items:center;gap:6px;padding:8px 12px;border-radius:8px;font-size:12px;background:rgba(239,68,68,0.1);border:1px solid rgba(239,68,68,0.3);color:var(--danger);text-decoration:none;transition:all 0.2s}
.btn-del:hover{background:var(--danger);color:#fff}
.empty{text-align:center;padding:80px;color:var(--muted)}
.empty i{font-size:48px;margin-bottom:16px;display:block;color:rgba(0,212,255,0.15)}
</style>
</head>
<body>
<% List<GameCard> cards = (List<GameCard>) request.getAttribute("cards"); User user = (User) session.getAttribute("user"); %>
<a href="<%= request.getContextPath() %>/mods" class="back-link"><i class="fas fa-arrow-left"></i> Retour</a>
<div class="page-title">Mes <span>Achats</span></div>
<p class="subtitle">Historique de vos acquisitions GameVerse</p>
<div class="cards-grid">
<% if (cards != null && !cards.isEmpty()) { for (GameCard gc : cards) { %>
  <div class="card-item">
    <div class="card-header">
      <div class="card-game"><%= gc.getModTitle() %></div>
      <span class="card-status">Confirme</span>
    </div>
    <div class="card-info">
      <div class="info-item"><div class="info-label">Acheteur</div><div class="info-val"><%= gc.getUserLogin() %></div></div>
      <div class="info-item"><div class="info-label">Carte</div><div class="info-val">**** <%= gc.getCardNumberLast4() %></div></div>
      <div class="info-item"><div class="info-label">Date</div><div class="info-val"><%= gc.getPurchasedAt() != null ? gc.getPurchasedAt().toString().substring(0,10) : "-" %></div></div>
      <div class="info-item"><div class="info-label">Montant</div><div class="card-price"><%= String.format("%.2f EUR", gc.getPrice()) %></div></div>
    </div>
    <div class="card-actions">
      <a href="<%= request.getContextPath() %>/cards?action=pdf&cardId=<%= gc.getId() %>" class="btn-pdf"><i class="fas fa-file-pdf"></i> Telecharger recu</a>
      <a href="<%= request.getContextPath() %>/cards?action=delete&id=<%= gc.getId() %>" class="btn-del" onclick="return confirm('Supprimer cet achat ?')"><i class="fas fa-trash"></i></a>
    </div>
  </div>
<% } } else { %>
  <div class="empty" style="grid-column:1/-1"><i class="fas fa-shopping-bag"></i><p>Aucun achat pour le moment.</p></div>
<% } %>
</div>
</body>
</html>
