
# Utiliser une image officielle de JDK 17 comme base
FROM eclipse-temurin:17-jdk-alpine

# Définir le répertoire de travail dans le conteneur
WORKDIR /app

# Copier le fichier JAR généré dans le conteneur
COPY target/Gestion-Cinema.jar app.jar

# Exposer le port sur lequel votre application fonctionne
EXPOSE 8081

# Commande pour exécuter l'application Spring Boot
ENTRYPOINT ["java", "-jar", "app.jar"]
