package es.arenzana.autoescuela.service;

import es.arenzana.autoescuela.dto.EstadisticasDTO;
import es.arenzana.autoescuela.dto.TestProgresoDTO;
import es.arenzana.autoescuela.model.*;
import es.arenzana.autoescuela.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class TestService {

    private static final String TEST_NO_ENCONTRADO = "Test no encontrado";

    private final TestRealizadoRepository testRealizadoRepository;
    private final RespuestaAlumnoRepository respuestaAlumnoRepository;
    private final PreguntaRepository preguntaRepository;
    private final TemaRepository temaRepository;
    private final RespuestaRepository respuestaRepository;

    public TestService(TestRealizadoRepository testRealizadoRepository,
                       RespuestaAlumnoRepository respuestaAlumnoRepository,
                       PreguntaRepository preguntaRepository,
                       TemaRepository temaRepository,
                       RespuestaRepository respuestaRepository) {
        this.testRealizadoRepository = testRealizadoRepository;
        this.respuestaAlumnoRepository = respuestaAlumnoRepository;
        this.preguntaRepository = preguntaRepository;
        this.temaRepository = temaRepository;
        this.respuestaRepository = respuestaRepository;
    }

    @Transactional
    public TestRealizado iniciarTest(Usuario alumno, Long temaId) {
        Optional<TestRealizado> testIncompleto = testRealizadoRepository
                .findFirstByAlumnoAndTemaIdAndCompletadoFalseOrderByFechaInicioDesc(alumno, temaId);

        if (testIncompleto.isPresent()) {
            return testIncompleto.get();
        }

        Tema tema = temaRepository.findById(temaId)
                .orElseThrow(() -> new RuntimeException("Tema no encontrado"));

        List<Pregunta> preguntas = preguntaRepository.findByTemaAndActivoTrue(tema);
        if (preguntas.isEmpty()) {
            throw new IllegalStateException("No hay preguntas disponibles para este tema");
        }

        TestRealizado test = new TestRealizado();
        test.setAlumno(alumno);
        test.setTema(tema);
        test.setFechaInicio(LocalDateTime.now());
        test.setCompletado(false);
        test.setTotalPreguntas(preguntas.size());
        test.setAciertos(0);
        test.setFallos(0);

        return testRealizadoRepository.save(test);
    }

    @Transactional
    public TestRealizado iniciarTestAleatorio(Usuario alumno, int numPreguntas) {
        List<Pregunta> preguntas = preguntaRepository.findRandomActive(numPreguntas);
        if (preguntas.isEmpty()) {
            throw new IllegalStateException("No hay preguntas disponibles");
        }

        TestRealizado test = new TestRealizado();
        test.setAlumno(alumno);
        test.setTema(null);
        test.setFechaInicio(LocalDateTime.now());
        test.setCompletado(false);
        test.setTotalPreguntas(preguntas.size());
        test.setAciertos(0);
        test.setFallos(0);
        test.setPreguntasAsignadas(new ArrayList<>(preguntas));

        return testRealizadoRepository.save(test);
    }

    @Transactional
    public void guardarRespuesta(Long testId, Long preguntaId, Long respuestaId) {
        TestRealizado test = testRealizadoRepository.findById(testId)
                .orElseThrow(() -> new RuntimeException(TEST_NO_ENCONTRADO));

        if (Boolean.TRUE.equals(test.getCompletado())) {
            throw new IllegalStateException("El test ya está completado");
        }

        Pregunta pregunta = preguntaRepository.findById(preguntaId)
                .orElseThrow(() -> new RuntimeException("Pregunta no encontrada"));

        Respuesta respuestaSeleccionada = respuestaRepository.findById(respuestaId)
                .orElseThrow(() -> new RuntimeException("Respuesta no encontrada"));

        Optional<RespuestaAlumno> respuestaExistente = respuestaAlumnoRepository
                .findByTestAndPreguntaId(test, preguntaId);

        boolean esCorrecta = respuestaSeleccionada.getCorrecta();

        if (respuestaExistente.isPresent()) {
            RespuestaAlumno oldRespuesta = respuestaExistente.get();
            if (Boolean.TRUE.equals(oldRespuesta.getCorrecta()) && !esCorrecta) {
                test.setAciertos(test.getAciertos() - 1);
                test.setFallos(test.getFallos() + 1);
            } else if (Boolean.FALSE.equals(oldRespuesta.getCorrecta()) && esCorrecta) {
                test.setAciertos(test.getAciertos() + 1);
                test.setFallos(test.getFallos() - 1);
            }
            oldRespuesta.setRespuestaSeleccionada(respuestaSeleccionada);
            oldRespuesta.setCorrecta(esCorrecta);
            oldRespuesta.setFechaRespuesta(LocalDateTime.now());
            respuestaAlumnoRepository.save(oldRespuesta);
        } else {
            RespuestaAlumno nuevaRespuesta = new RespuestaAlumno();
            nuevaRespuesta.setTest(test);
            nuevaRespuesta.setPregunta(pregunta);
            nuevaRespuesta.setRespuestaSeleccionada(respuestaSeleccionada);
            nuevaRespuesta.setCorrecta(esCorrecta);

            if (esCorrecta) {
                test.setAciertos(test.getAciertos() + 1);
            } else {
                test.setFallos(test.getFallos() + 1);
            }

            respuestaAlumnoRepository.save(nuevaRespuesta);
        }

        testRealizadoRepository.save(test);
    }

    @Transactional
    public TestRealizado completarTest(Long testId) {
        TestRealizado test = testRealizadoRepository.findById(testId)
                .orElseThrow(() -> new RuntimeException(TEST_NO_ENCONTRADO));

        if (Boolean.TRUE.equals(test.getCompletado())) {
            return test;
        }

        test.setCompletado(true);
        test.setFechaFin(LocalDateTime.now());

        if (test.getTotalPreguntas() > 0) {
            int puntuacion = (test.getAciertos() * 100) / test.getTotalPreguntas();
            test.setPuntuacionTotal(puntuacion);
        }

        return testRealizadoRepository.save(test);
    }

    @Transactional(readOnly = true)
    public TestProgresoDTO obtenerProgresoTest(Long testId) {
        TestRealizado test = testRealizadoRepository.findById(testId)
                .orElseThrow(() -> new RuntimeException(TEST_NO_ENCONTRADO));

        List<Pregunta> preguntas = test.getTema() != null
                ? preguntaRepository.findByTemaAndActivoTrue(test.getTema())
                : test.getPreguntasAsignadas();

        Map<Long, RespuestaAlumno> respuestasMap = respuestaAlumnoRepository.findByTest(test)
                .stream()
                .collect(Collectors.toMap(ra -> ra.getPregunta().getId(), ra -> ra));

        TestProgresoDTO dto = new TestProgresoDTO();
        boolean completado = Boolean.TRUE.equals(test.getCompletado());
        dto.setTestId(test.getId());
        dto.setTemaNombre(test.getTema() != null ? test.getTema().getNombre() : "Aleatorio");
        dto.setTotalPreguntas(preguntas.size());
        dto.setPreguntasRespondidas(respuestasMap.size());
        dto.setCompletado(completado);
        dto.setAciertos(test.getAciertos());
        dto.setFallos(test.getFallos());
        dto.setPuntuacion(test.getPuntuacionTotal() != null ? test.getPuntuacionTotal() : 0);

        List<TestProgresoDTO.PreguntaConRespuesta> preguntasDTO = new ArrayList<>();

        for (Pregunta pregunta : preguntas) {
            TestProgresoDTO.PreguntaConRespuesta pDto = new TestProgresoDTO.PreguntaConRespuesta();

            TestProgresoDTO.PreguntaConRespuesta.PreguntaSimple pSimple =
                    new TestProgresoDTO.PreguntaConRespuesta.PreguntaSimple();
            pSimple.setId(pregunta.getId());
            pSimple.setTexto(pregunta.getTexto());
            pSimple.setExplicacion(pregunta.getExplicacion());
            pDto.setPregunta(pSimple);

            List<TestProgresoDTO.PreguntaConRespuesta.RespuestaSimple> respuestasSimple =
                    pregunta.getRespuestas().stream()
                            .map(r -> {
                                TestProgresoDTO.PreguntaConRespuesta.RespuestaSimple rs =
                                        new TestProgresoDTO.PreguntaConRespuesta.RespuestaSimple();
                                rs.setId(r.getId());
                                rs.setTexto(r.getTexto());
                                if (completado) rs.setEsCorrecta(r.getCorrecta());
                                return rs;
                            })
                            .collect(Collectors.toList());
            pDto.setRespuestas(respuestasSimple);

            RespuestaAlumno ra = respuestasMap.get(pregunta.getId());
            if (ra != null) {
                pDto.setRespuestaSeleccionadaId(ra.getRespuestaSeleccionada().getId());
                pDto.setFueCorrecta(ra.getCorrecta());
            }

            preguntasDTO.add(pDto);
        }

        dto.setPreguntas(preguntasDTO);
        return dto;
    }

    @Transactional(readOnly = true)
    public EstadisticasDTO obtenerEstadisticas(Usuario alumno) {
        EstadisticasDTO stats = new EstadisticasDTO();

        long totalTests = testRealizadoRepository.countTestsCompletadosByAlumno(alumno);
        int totalAciertos = testRealizadoRepository.sumAciertosByAlumno(alumno);
        int totalFallos = testRealizadoRepository.sumFallosByAlumno(alumno);
        int totalPreguntas = totalAciertos + totalFallos;

        stats.setTotalTests(totalTests);
        stats.setTotalAciertos(totalAciertos);
        stats.setTotalFallos(totalFallos);
        stats.setTotalPreguntasRespondidas(totalPreguntas);

        if (totalPreguntas > 0) {
            double porcentaje = (totalAciertos * 100.0) / totalPreguntas;
            stats.setPorcentajeAciertos(Math.round(porcentaje * 100.0) / 100.0);
        }

        Map<String, EstadisticasDTO.TemaEstadistica> statsPorTema = new HashMap<>();
        Map<String, Integer> sumaPuntuaciones = new HashMap<>();
        List<TestRealizado> tests = testRealizadoRepository.findByAlumnoAndCompletadoTrue(alumno);
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        for (TestRealizado test : tests) {
            if (test.getTema() == null) continue;
            String temaNombre = test.getTema().getNombre();
            EstadisticasDTO.TemaEstadistica temaStat = statsPorTema.computeIfAbsent(temaNombre, k -> {
                EstadisticasDTO.TemaEstadistica ts = new EstadisticasDTO.TemaEstadistica();
                ts.setTemaNombre(temaNombre);
                ts.setTestsRealizados(0);
                ts.setAciertos(0);
                ts.setFallos(0);
                return ts;
            });

            temaStat.setTestsRealizados(temaStat.getTestsRealizados() + 1);
            temaStat.setAciertos(temaStat.getAciertos() + test.getAciertos());
            temaStat.setFallos(temaStat.getFallos() + test.getFallos());

            int totalRespuestasTema = temaStat.getAciertos() + temaStat.getFallos();
            if (totalRespuestasTema > 0) {
                double porcentajeTema = (temaStat.getAciertos() * 100.0) / totalRespuestasTema;
                temaStat.setPorcentajeAciertos(Math.round(porcentajeTema * 100.0) / 100.0);
            }

            int puntuacion = test.getPuntuacionTotal() != null ? test.getPuntuacionTotal() : 0;
            sumaPuntuaciones.merge(temaNombre, puntuacion, Integer::sum);

            if (puntuacion > temaStat.getMejorPuntuacion()) {
                temaStat.setMejorPuntuacion(puntuacion);
                if (test.getFechaFin() != null) {
                    temaStat.setFechaMejorPuntuacion(test.getFechaFin().format(fmt));
                }
            }
        }

        statsPorTema.forEach((nombre, ts) -> {
            if (ts.getTestsRealizados() > 0) {
                double media = sumaPuntuaciones.getOrDefault(nombre, 0) * 1.0 / ts.getTestsRealizados();
                ts.setMediaAciertos(Math.round(media * 100.0) / 100.0);
            }
        });

        stats.setEstadisticasPorTema(statsPorTema);
        return stats;
    }

    public List<TestRealizado> obtenerUltimosTests(Usuario alumno, int limite) {
        return testRealizadoRepository.findByAlumnoOrderByFechaInicioDesc(alumno)
                .stream()
                .filter(t -> Boolean.TRUE.equals(t.getCompletado()))
                .limit(limite)
                .toList();
    }
}
