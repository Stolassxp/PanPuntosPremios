import 'dart:convert';
import 'package:flutter/material.dart';
import 'package:http/http.dart' as http;

void main() => runApp(const SaldoApp());

class SaldoApp extends StatelessWidget {
  const SaldoApp({super.key});

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'Pan, Puntos y Premios',
      theme: ThemeData(colorSchemeSeed: Colors.orange, useMaterial3: true),
      home: const PantallaSaldo(),
    );
  }
}

class PantallaSaldo extends StatefulWidget {
  const PantallaSaldo({super.key});

  @override
  State<PantallaSaldo> createState() => _PantallaSaldoState();
}

class _PantallaSaldoState extends State<PantallaSaldo> {
  static const String _urlBase = 'http://localhost:8080/PanPuntosPremios/api/saldo';

  final TextEditingController _controlador = TextEditingController();
  bool _cargando = false;
  String? _mensaje;
  Map<String, dynamic>? _datos;

  @override
  void dispose() {
    _controlador.dispose();
    super.dispose();
  }

  Future<void> _consultar() async {
    final identificador = _controlador.text.trim();
    if (identificador.isEmpty) {
      setState(() {
        _datos = null;
        _mensaje = 'Ingrese su identificador';
      });
      return;
    }
    setState(() {
      _cargando = true;
      _datos = null;
      _mensaje = null;
    });
    Map<String, dynamic>? datos;
    String? mensaje;
    try {
      final uri = Uri.parse(_urlBase).replace(queryParameters: {'identificador': identificador});
      final respuesta = await http.get(uri).timeout(const Duration(seconds: 5));
      final json = jsonDecode(utf8.decode(respuesta.bodyBytes)) as Map<String, dynamic>;
      if (respuesta.statusCode >= 400) {
        mensaje = json['error']?.toString() ?? 'Error al consultar';
      } else {
        datos = json;
      }
    } catch (_) {
      mensaje = 'No fue posible conectar con el servidor';
    }
    if (!mounted) return;
    setState(() {
      _cargando = false;
      _datos = datos;
      _mensaje = mensaje;
    });
  }

  Widget _tarjeta(Map<String, dynamic> datos) {
    final activo = datos['activo'] == true;
    return Card(
      child: Padding(
        padding: const EdgeInsets.all(16),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Text(datos['nombre'].toString(), style: Theme.of(context).textTheme.titleLarge),
            const SizedBox(height: 8),
            Text('${datos['saldoPuntos']} puntos', style: Theme.of(context).textTheme.headlineMedium),
            const SizedBox(height: 8),
            Text(activo ? 'Estado: Activo' : 'Estado: Inactivo'),
          ],
        ),
      ),
    );
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: SafeArea(
        child: Center(
          child: SingleChildScrollView(
            child: ConstrainedBox(
              constraints: const BoxConstraints(maxWidth: 420),
              child: Padding(
                padding: const EdgeInsets.all(24),
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.stretch,
                  children: [
                    Text(
                      'Pan, Puntos y Premios',
                      style: Theme.of(context).textTheme.headlineMedium,
                      textAlign: TextAlign.center,
                    ),
                    const SizedBox(height: 8),
                    const Text('Consulta tu saldo de puntos', textAlign: TextAlign.center),
                    const SizedBox(height: 24),
                    TextField(
                      controller: _controlador,
                      keyboardType: TextInputType.number,
                      decoration: const InputDecoration(
                        border: OutlineInputBorder(),
                        labelText: 'Identificador',
                      ),
                      onSubmitted: (_) => _consultar(),
                    ),
                    const SizedBox(height: 16),
                    FilledButton(
                      onPressed: _cargando ? null : _consultar,
                      child: const Text('Consultar saldo'),
                    ),
                    const SizedBox(height: 24),
                    if (_cargando) const Center(child: CircularProgressIndicator()),
                    if (_mensaje != null)
                      Text(
                        _mensaje!,
                        textAlign: TextAlign.center,
                        style: TextStyle(color: Theme.of(context).colorScheme.error),
                      ),
                    if (_datos != null) _tarjeta(_datos!),
                  ],
                ),
              ),
            ),
          ),
        ),
      ),
    );
  }
}