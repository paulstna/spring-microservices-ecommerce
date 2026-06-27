.PHONY: build up start down logs rebuild

# ── Paths ────────────────────────────────────────────────
COMPOSE_FILE = docker/default/compose.yaml

# ── Build Docker images ──────────────────────────────────
build:
	@echo ">>> Building infrastructure services..."
	cd infrastructure/config-server  && mvn jib:dockerBuild -q
	cd infrastructure/eureka-server  && mvn jib:dockerBuild -q
	cd infrastructure/gateway-server && mvn jib:dockerBuild -q

	@echo ">>> Building business services..."
	cd services/inventory    && mvn jib:dockerBuild -q
	cd services/user         && mvn jib:dockerBuild -q
	cd services/payment      && mvn jib:dockerBuild -q
	cd services/delivery     && mvn jib:dockerBuild -q
	cd services/order        && mvn jib:dockerBuild -q
	cd services/notification && mvn jib:dockerBuild -q
	cd services/cart         && mvn jib:dockerBuild -q

# ── Build images and start containers ───────────────────
up: build
	docker compose -f $(COMPOSE_FILE) up -d

# ── Start containers without rebuilding images ──────────
start:
	docker compose -f $(COMPOSE_FILE) up -d

# ── Stop and remove containers ──────────────────────────
down:
	docker compose -f $(COMPOSE_FILE) down

# ── Stop and remove containers and volumes ──────────────
down-v:
	docker compose -f $(COMPOSE_FILE) down -v

# ── Stop and remove containers, volumes and images ──────
down-vi:
	docker compose -f $(COMPOSE_FILE) down -v --rmi all

# ── Follow container logs ───────────────────────────────
logs:
	docker compose -f $(COMPOSE_FILE) logs -f

# ── Rebuild a specific service ──────────────────────────
# Usage: make rebuild svc=infrastructure/eureka-server
rebuild:
	cd $(svc) && mvn jib:dockerBuild -q
	docker compose -f $(COMPOSE_FILE) up -d --no-deps $(notdir $(svc))