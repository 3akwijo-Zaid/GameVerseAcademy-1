<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="ma.ac.esi.gameverseacademy.model.User" %>
<!DOCTYPE html>
<html>
<head>
    <title>Panel Administration</title>
    <style>
        body { font-family: sans-serif; background: #0b0e14; color: white; padding: 50px; text-align: center; }
        .card { background: #161b22; padding: 30px; border-radius: 15px; border: 1px solid #4A90D9; display: inline-block; }
    </style>
</head>
<body>
    <div class="card">
        <h1>Zone Administrateur</h1>
        <% User u = (User) session.getAttribute("user"); %>
        <p>Bienvenue Maître <strong><%= u != null ? u.getLogin() : "Inconnu" %></strong></p>
        <p>Statut : <span style="color: #4A90D9;"><%= u != null ? u.getRole() : "N/A" %></span></p>
        <a href="mods" style="color: #8b949e; text-decoration: none;">← Retour à la liste</a>
    </div>
</body>
</html>