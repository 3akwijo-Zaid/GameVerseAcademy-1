<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List, ma.ac.esi.gameverseacademy.model.Payment" %>
<!DOCTYPE html>
<html lang="fr">
<head>
<meta charset="UTF-8"><meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>Paiements — GameVerse Admin</title>
<link href="https://fonts.googleapis.com/css2?family=Rajdhani:wght@600;700&family=Inter:wght@400;500&display=swap" rel="stylesheet">
<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css">
<style>
*,*::before,*::after{box-sizing:border-box;margin:0;padding:0}
:root{--bg:#060810;--surface:#0e1117;--surface2:#151b26;--border:rgba(255,255,255,0.06);--accent:#00d4ff;--text:#e2e8f0;--muted:#64748b;--success:#10b981}
body{font-family:'Inter',sans-serif;background:var(--bg);color:var(--text);min-height:100vh;padding:40px}
.page-title{font-family:'Rajdhani',sans-serif;font-size:32px;font-weight:700;margin-bottom:8px}
.page-title span{color:var(--accent)}
.back-link{display:inline-flex;align-items:center;gap:6px;color:var(--muted);font-size:13px;text-decoration:none;margin-bottom:24px;transition:color 0.2s}
.back-link:hover{color:var(--accent)}
table{width:100%;max-width:1200px;border-collapse:collapse}
th{text-align:left;padding:12px 16px;font-size:11px;text-transform:uppercase;letter-spacing:1px;color:var(--muted);border-bottom:1px solid var(--border)}
td{padding:12px 16px;font-size:13px;border-bottom:1px solid var(--border)}
tr:hover td{background:rgba(255,255,255,0.02)}
.status-badge{padding:3px 10px;border-radius:20px;font-size:11px;font-weight:600;background:rgba(16,185,129,0.15);color:var(--success);border:1px solid rgba(16,185,129,0.3)}
.total-row td{font-weight:700;color:var(--accent);border-top:2px solid var(--border)}
</style>
</head>
<body>
<% List<Payment> payments = (List<Payment>) request.getAttribute("payments");
   double total = 0; if(payments!=null) for(Payment p:payments) total+=p.getAmount(); %>
<a href="<%= request.getContextPath() %>/mods" class="back-link"><i class="fas fa-arrow-left"></i> Retour</a>
<div class="page-title">Gestion des <span>Paiements</span></div>
<table>
  <thead><tr><th>#</th><th>Jeu</th><th>Utilisateur</th><th>Reference</th><th>Date</th><th>Statut</th><th>Montant</th></tr></thead>
  <tbody>
<% if(payments!=null) { for(Payment p:payments) { %>
  <tr><td><%= p.getId() %></td><td><%= p.getModTitle() %></td><td><%= p.getUserLogin() %></td><td style="font-family:monospace;color:var(--muted)"><%= p.getTransactionRef() %></td><td><%= p.getPaymentDate() != null ? p.getPaymentDate().toString().substring(0,16) : "-" %></td><td><span class="status-badge"><%= p.getStatus() %></span></td><td style="color:var(--success);font-weight:600"><%= String.format("%.2f EUR", p.getAmount()) %></td></tr>
<% } } %>
  <tr class="total-row"><td colspan="6">TOTAL</td><td><%= String.format("%.2f EUR", total) %></td></tr>
  </tbody>
</table>
</body>
</html>
