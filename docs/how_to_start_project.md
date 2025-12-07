racine du projet

cd /mnt/d/AutoFormations/AutoFormation_2023/Gestion-Cinema/cinema-api

lancer docker daemon

sudo dockerd

utiliser uniquement

docker-compose stop
docker-compose up -d



pour tchecker le conteneur base de données

docker ps -a

identifier l'id du conteneur

docker exec -it <id> bash
root@c1a581e83e46:/# psql -U postgres

postgres=# \c db_cinema

db_cinema=# \d film