CREATE TABLE IF NOT EXISTS categoria (
    id_categoria SERIAL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL
);

CREATE TABLE IF NOT EXISTS producto (
    id_producto SERIAL PRIMARY KEY,
    nombre VARCHAR(255) NOT NULL,
    descripcion TEXT,
    precio DECIMAL(10, 2),
    stock INT,
    estado VARCHAR(50),
    id_categoria INTEGER,
    FOREIGN KEY (id_categoria) REFERENCES categoria (id_categoria) ON DELETE CASCADE
    );

CREATE TABLE IF NOT EXISTS cliente (
                                       id_cliente SERIAL PRIMARY KEY,
                                       nombre VARCHAR(255) NOT NULL,
                                       direccion VARCHAR(255),
                                       email VARCHAR(100),
                                       telefono VARCHAR(20),
                                       clave VARCHAR(20),
                                       rol VARCHAR(20)
);


CREATE TABLE IF NOT EXISTS comunas_santiago (
    id SERIAL PRIMARY KEY,
    cod_comuna INT,
    comuna VARCHAR(50),
    provincia VARCHAR(50),
    region VARCHAR(50),
    geom GEOMETRY(POLYGON, 4326),
    pago VARCHAR(50)
    );

CREATE TABLE IF NOT EXISTS pedido (
                                      id_pedido SERIAL PRIMARY KEY,
                                      id_zona SERIAL,
                                      id_cliente INTEGER NULL,
                                      coordenada_direccion GEOMETRY(POINT, 4326),
    direccion VARCHAR(255),
    estado VARCHAR(50),
    FOREIGN KEY (id_zona) REFERENCES comunas_santiago (id) ON DELETE CASCADE,
    FOREIGN KEY (id_cliente) REFERENCES cliente (id_cliente) ON DELETE CASCADE
    );

CREATE TABLE IF NOT EXISTS orden (
                                     id_orden SERIAL PRIMARY KEY,
                                     fecha_orden TIMESTAMP,
                                     estado VARCHAR(50),
    id_pedido INTEGER,
    id_cliente INTEGER,
    total DECIMAL(10, 2),
    FOREIGN KEY (id_pedido) REFERENCES pedido (id_pedido) ON DELETE CASCADE,
    FOREIGN KEY (id_cliente) REFERENCES cliente (id_cliente) ON DELETE CASCADE
    );

CREATE TABLE IF NOT EXISTS detalle_orden (
                                             id_detalle SERIAL PRIMARY KEY,
                                             id_orden INTEGER,
                                             id_producto INTEGER,
                                             cantidad INT,
                                             precio_unitario DECIMAL(10, 2),
    FOREIGN KEY (id_orden) REFERENCES orden (id_orden) ON DELETE CASCADE,
    FOREIGN KEY (id_producto) REFERENCES producto (id_producto) ON DELETE CASCADE
    );

CREATE TABLE IF NOT EXISTS auditoria (
                                         id SERIAL PRIMARY KEY,
                                         id_objeto INTEGER,
                                         nombre_tabla VARCHAR(100),
    operacion VARCHAR(200),
    id_cliente INTEGER,
    fecha TIMESTAMP
    );

CREATE OR REPLACE FUNCTION auditar_operacion()
RETURNS TRIGGER AS $BODY$
DECLARE
id_valor INTEGER;
    id_cliente INTEGER;
BEGIN
    IF (TG_TABLE_NAME = 'orden') THEN
        IF (TG_OP = 'DELETE') THEN
SELECT OLD.id_orden INTO id_valor;
SELECT OLD.id_cliente INTO id_cliente;
ELSE
SELECT NEW.id_orden INTO id_valor;
SELECT NEW.id_cliente INTO id_cliente;
END IF;
    ELSIF (TG_TABLE_NAME = 'detalle_orden') THEN
        IF (TG_OP = 'DELETE') THEN
SELECT OLD.id_detalle INTO id_valor;
SELECT o.id_cliente INTO id_cliente
FROM orden o WHERE OLD.id_orden = o.id_orden;
ELSE
SELECT NEW.id_detalle INTO id_valor;
SELECT o.id_cliente INTO id_cliente
FROM orden o WHERE NEW.id_orden = o.id_orden;
END IF;
END IF;

INSERT INTO auditoria (id_objeto, nombre_tabla, operacion, id_cliente, fecha)
VALUES (id_valor, TG_TABLE_NAME, TG_OP, id_cliente, current_timestamp);

RETURN NULL;
END;
$BODY$
LANGUAGE plpgsql;
/

CREATE OR REPLACE FUNCTION actualizar_estado_orden(idorden INTEGER)
    RETURNS VARCHAR AS $$
DECLARE
cantidadp INT;
    stockproducto INT;
    idproducto INT;
    stockinsuficiente BOOLEAN := FALSE;
    estadoorden VARCHAR;
BEGIN
FOR idproducto, cantidadp IN
SELECT detalle.id_producto, detalle.cantidad
FROM detalle_orden detalle
WHERE detalle.id_orden = idorden
    LOOP
SELECT prod.stock INTO stockproducto
FROM producto prod
WHERE prod.id_producto = idproducto;

IF stockproducto < cantidadp THEN
                stockinsuficiente := TRUE;
                EXIT;
END IF;
END LOOP;

    IF stockinsuficiente THEN
UPDATE orden
SET estado = 'pendiente'
WHERE id_orden = idorden;
estadoorden := 'pendiente';
ELSE
        FOR idproducto, cantidadp IN
SELECT detalle.id_producto, detalle.cantidad
FROM detalle_orden detalle
WHERE detalle.id_orden = idorden
    LOOP
UPDATE producto
SET stock = stock - cantidadp
WHERE id_producto = idproducto;

-- Actualizar el estado del producto si el stock es 0
IF (SELECT prod.stock FROM producto prod WHERE prod.id_producto = idproducto LIMIT 1) = 0 THEN
UPDATE producto
SET estado = 'agotado'
WHERE id_producto = idproducto;
END IF;
END LOOP;

UPDATE orden
SET estado = 'enviada'
WHERE id_orden = idorden;
estadoorden := 'enviada';
END IF;

RETURN estadoorden;
END;
$$ LANGUAGE plpgsql;
/

CREATE OR REPLACE FUNCTION calcular_total_orden(p_id_orden BIGINT)
       RETURNS DECIMAL(10,2) AS $BODY$
DECLARE
v_total DECIMAL(10,2);
BEGIN
SELECT SUM(d.cantidad * d.precio_unitario)
INTO v_total
FROM detalle_orden d
WHERE d.id_orden = p_id_orden;

UPDATE orden
SET total = v_total
where id_orden = p_id_orden;

RETURN v_total;
END;
$BODY$ LANGUAGE plpgsql;
/

CREATE OR REPLACE FUNCTION get_user_most_operations()
RETURNS TABLE (
    id_usuario INTEGER,
    tipo_operacion VARCHAR(200),
    total_operaciones BIGINT
) AS $BODY$
BEGIN
RETURN QUERY
    WITH ranked_operations AS (
        SELECT
            id_cliente,
            operacion,
            COUNT(*) as cantidad,
            RANK() OVER (PARTITION BY operacion ORDER BY COUNT(*) DESC) as rank
        FROM auditoria
        WHERE operacion IN ('INSERT', 'DELETE', 'UPDATE')
        GROUP BY id_cliente, operacion
    )
SELECT
    id_cliente,
    operacion,
    cantidad
FROM ranked_operations
WHERE rank = 1;

RETURN;
END;
$BODY$
LANGUAGE plpgsql;
/

CREATE OR REPLACE FUNCTION verificar_y_actualizar_estado_pedido(p_id_pedido INTEGER)
RETURNS BOOLEAN AS $BODY$
DECLARE
v_coordenada GEOMETRY(POINT, 4326);
    v_geom_comuna GEOMETRY(POLYGON, 4326);
    v_en_rango BOOLEAN;
    v_id_zona INTEGER;
BEGIN
    -- Obtener la coordenada del pedido
SELECT p.coordenada_direccion
INTO v_coordenada
FROM pedido p
WHERE p.id_pedido = p_id_pedido;

-- Iterar sobre todas las comunas para verificar si la coordenada está dentro de alguna
FOR v_id_zona, v_geom_comuna IN
SELECT c.id, c.geom
FROM comunas_santiago c
    LOOP
        -- Verificar si la coordenada del pedido está dentro del polígono de la comuna
        v_en_rango := ST_Covers(v_geom_comuna, v_coordenada);

-- Si está en rango, actualizar el estado del pedido y el id_zona
IF v_en_rango THEN
UPDATE pedido
SET estado = 'en rango', id_zona = v_id_zona
WHERE id_pedido = p_id_pedido;
RETURN TRUE; -- Devuelve TRUE si está en rango
END IF;
END LOOP;

    -- Si no se encontró ninguna comuna que contenga la coordenada
UPDATE pedido
SET estado = 'fuera de rango', id_zona = NULL
WHERE id_pedido = p_id_pedido;

RETURN FALSE; -- Devuelve FALSE si no está en rango
END;
$BODY$ LANGUAGE plpgsql;
/


CREATE OR REPLACE FUNCTION obtener_repartidores_por_comuna(nombre_comuna VARCHAR)
RETURNS TABLE(id_cliente INT, nombre VARCHAR) AS
$$
BEGIN
RETURN QUERY
SELECT c.id_cliente, c.nombre
FROM pedido p
         JOIN cliente c ON p.id_cliente = c.id_cliente
         JOIN comunas_santiago cs ON p.id_zona = cs.id
WHERE p.estado = 'entregado'
  AND ST_Within(p.coordenada_direccion, cs.geom)
  AND cs.comuna = nombre_comuna
  AND cs.geom IS NOT NULL;
END;
$$ LANGUAGE plpgsql;
/

CREATE OR REPLACE FUNCTION es_ubicacion_restringida(p_id_pedido INTEGER)
RETURNS BOOLEAN AS $$
DECLARE
v_coordenada GEOMETRY(POINT, 4326);
    v_geom_comuna GEOMETRY(POLYGON, 4326);
    v_en_rango BOOLEAN;
    v_pago VARCHAR(50);
BEGIN
    -- Obtener la coordenada del pedido y tipo de pago de la zona
SELECT p.coordenada_direccion, c.pago
INTO v_coordenada, v_pago
FROM pedido p
         JOIN comunas_santiago c ON p.id_zona = c.id
WHERE p.id_pedido = p_id_pedido;

-- Obtener la geometría de la comuna asociada al pedido
SELECT c.geom
INTO v_geom_comuna
FROM pedido p
         JOIN comunas_santiago c ON p.id_zona = c.id
WHERE p.id_pedido = p_id_pedido;

-- Verificar si la coordenada está dentro del polígono
v_en_rango := ST_Covers(v_geom_comuna, v_coordenada);

    -- Actualizar estado y retornar resultado
    IF v_en_rango AND v_pago != 'RESTRINGIDO' THEN
UPDATE pedido
SET estado = 'en_rango'
WHERE id_pedido = p_id_pedido;
RETURN FALSE;
ELSE
UPDATE pedido
SET estado = 'fuera_rango'
WHERE id_pedido = p_id_pedido;
RETURN TRUE;
END IF;
END;
$$
LANGUAGE plpgsql;
/

CREATE OR REPLACE FUNCTION es_area_cobertura(p_id_pedido INTEGER)
RETURNS BOOLEAN AS $$
DECLARE
v_estado_pago VARCHAR(50);
    v_en_cobertura BOOLEAN;
    v_coordenada GEOMETRY(POINT, 4326);
    v_geom_comuna GEOMETRY(POLYGON, 4326);
BEGIN
    -- Obtener el estado de pago del pedido
SELECT c.pago
INTO v_estado_pago
FROM pedido p
         JOIN comunas_santiago c ON p.id_zona = c.id
WHERE p.id_pedido = p_id_pedido;

-- Obtener la coordenada del pedido
SELECT p.coordenada_direccion
INTO v_coordenada
FROM pedido p
WHERE p.id_pedido = p_id_pedido;

-- Obtener la geometría de la comuna asociada al pedido
SELECT c.geom
INTO v_geom_comuna
FROM pedido p
         JOIN comunas_santiago c ON p.id_zona = c.id
WHERE p.id_pedido = p_id_pedido;

-- Verificar si la coordenada del pedido está dentro del polígono de la comuna
v_en_cobertura := ST_Covers(v_geom_comuna, v_coordenada);

    -- Retornar true si está en cobertura y es gratuito, false en caso contrario
RETURN v_en_cobertura AND v_estado_pago = 'GRATUITO';
END;
$$
LANGUAGE plpgsql;
/

DROP TRIGGER IF EXISTS trigger_auditoria_orden ON orden;
CREATE TRIGGER trigger_auditoria_orden
    AFTER INSERT OR UPDATE OR DELETE ON orden
    FOR EACH ROW
EXECUTE FUNCTION auditar_operacion();

DROP TRIGGER IF EXISTS trigger_auditoria_detalle_orden ON detalle_orden;
CREATE TRIGGER trigger_auditoria_detalle_orden
    AFTER INSERT OR UPDATE OR DELETE ON detalle_orden
    FOR EACH ROW
EXECUTE FUNCTION auditar_operacion();