-- ============================================================
-- Datos de ejemplo para glucoAPI
-- Ejecutar solo si la base esta vacia (o si aceptas duplicados).
-- Usa INSERT IGNORE con IDs fijos para que sea seguro repetirlo.
-- ============================================================

USE gluco_api;

INSERT IGNORE INTO usuario
  (idUsuario, nombre, primerApellido, segundoApellido, email)
VALUES
  (1, 'Ana',  'Garcia', 'Lopez', 'ana@example.com'),
  (2, 'Luis', 'Perez',  NULL,    'luis@example.com');

INSERT IGNORE INTO medida
  (idMedida, idUsuario, glucosa, fechaHora, contexto, notas)
VALUES
  (1, 1,  95, NOW(), 'Ayunas',       'Medicion de la manana'),
  (2, 1, 140, NOW(), 'Postprandial', 'Despues del almuerzo'),
  (3, 2, 110, NOW(), 'Ayunas',       NULL);
