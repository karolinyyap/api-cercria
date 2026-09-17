package br.com.gestaocercria.api.controle;

import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRMapCollectionDataSource;

import org.springframework.http.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/relatorio")
@CrossOrigin(origins = "*")
public class ControleRelatorio {

    private final JdbcTemplate jdbcTemplate;

    public ControleRelatorio(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @PostMapping("/gerar")
    public ResponseEntity<byte[]> gerarRelatorio(
            @RequestBody Map<String, String> dados) {

        try {

            String tipo = dados.get("tipo");

            if (tipo == null || tipo.isBlank()) {
                return ResponseEntity.badRequest().build();
            }

            String sql = obterSql(tipo);
            String arquivoJrxml = obterArquivoJrxml(tipo);

            if (sql == null || arquivoJrxml == null) {
                return ResponseEntity.badRequest().build();
            }

            // Localiza o modelo JRXML
            InputStream jrxml = getClass().getClassLoader().getResourceAsStream("relatorios/" + arquivoJrxml);

            if (jrxml == null) {
                throw new RuntimeException(
                        "Arquivo JRXML não encontrado: " + arquivoJrxml
                );
            }

            // Compila o modelo
            JasperReport report = JasperCompileManager.compileReport(jrxml);

            // Passa os dados do SQL para o Jasper
            List<Map<String, Object>> resultado = jdbcTemplate.queryForList(sql);

            List<Map<String, ?>> dadosJasper = new java.util.ArrayList<>(resultado);

            JRMapCollectionDataSource dataSource = new JRMapCollectionDataSource(dadosJasper);

            // Gera o relatório
            JasperPrint print = JasperFillManager.fillReport(report, Map.of(), dataSource);

            // Converte para PDF
            byte[] pdf = JasperExportManager.exportReportToPdf(print);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "inline; filename=relatorio-" + tipo + ".pdf"
                    ).contentType(MediaType.APPLICATION_PDF).body(pdf);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    private String obterSql(String tipo) {
        return switch (tipo) {

            case "acolhidos" -> """
                SELECT
                    a.nome,
                    a.cpf,
                    a.data_nascimento,
                    a.escola,
                    a.local_familiar,
                    a.numero_processo,
                    a.vara,
                    a.data_entrada,
                    a.data_saida,
                    a.cor_pele,
                    a.deficiencia,
                    a.ppcaam
                FROM acolhido a
                WHERE a.excluido = false
                ORDER BY a.nome
                """;

            case "funcionarios" -> """
                SELECT
                    f.nome,
                    f.cpf,
                    f.telefone,
                    f.data_nascimento,
                    f.email,
                    f.rg,
                    f.orgao_emissor,
                    f.uf,
                    f.cargo,
                    f.escolaridade,
                    f.carga_horaria,
                    f.sexo,
                    f.data_admissao,
                    f.data_saida
                FROM funcionario f
                WHERE f.excluido = false
                ORDER BY f.nome
                """;

            case "medicamentos" -> """
                SELECT
                    m.nome,
                    m.categoria,
                    m.unidade_medida
                FROM medicamento m
                WHERE m.excluido = false
                ORDER BY m.nome
                """;

            case "produtos" -> """
                SELECT
                    p.nome,
                    p.categoria,
                    p.unidade_medida
                FROM produto p
                WHERE p.excluido = false
                ORDER BY p.nome
                """;

            case "estoqueMedicamentos" -> """
                SELECT
                    m.nome AS medicamento,
                    m.categoria,
                    e.quantidade,
                    e.quantidade_atual,
                    e.data_validade,
                    e.origem,
                    f.nome AS responsavel,
                    e.data_entrada
                FROM estoque_medicamento e
                LEFT JOIN medicamento m
                    ON m.id = e.medicamento_id
                LEFT JOIN funcionario f
                    ON f.id = e.funcionario_id
                ORDER BY m.nome
                """;

            case "entradas" -> """
                SELECT
                    p.nome AS produto,
                    p.categoria,
                    e.quantidade,
                    e.data_entrada,
                    e.data_validade,
                    e.origem,
                    f.nome AS responsavel,
                    e.observacao,
                    e.quantidade_atual
                FROM entrada_produto e
                LEFT JOIN produto p
                    ON p.id = e.produto_id
                LEFT JOIN funcionario f
                    ON f.id = e.funcionario_id
                ORDER BY e.data_entrada DESC
                """;

            case "saidas" -> """
                SELECT
                    p.nome AS produto,
                    p.categoria,
                    s.quantidade,
                    s.data_saida,
                    s.motivo,
                    f.nome AS responsavel
                FROM saida_produto s
                LEFT JOIN produto p
                    ON p.id = s.produto_id
                LEFT JOIN funcionario f
                    ON f.id = s.funcionario_id
                ORDER BY s.data_saida DESC
                """;

            case "usoMedicamentos" -> """
                SELECT
                    m.nome AS medicamento,
                    a.nome AS acolhido,
                    c.dose,
                    c.intervalo,
                    c.iniciando_em,
                    c.vezes_ao_dia,
                    c.horario_fixo,
                    c.dias_semana,
                    c.data_inicio,
                    c.data_fim,
                    c.uso_continuo,
                    c.observacao,
                    f.nome AS funcionario_cadastro
                FROM controle_uso_medicamento c
                LEFT JOIN medicamento m
                    ON m.id = c.medicamento_id
                LEFT JOIN acolhido a
                    ON a.id = c.acolhido_id
                LEFT JOIN funcionario f
                    ON f.id = c.funcionario_cadastro_id
                ORDER BY a.nome, m.nome
                """;

            case "agendaMedicamentos" -> """
                SELECT
                    am.data,
                    am.horario,
                    m.nome AS medicamento,
                    a.nome AS acolhido,
                    am.dose,
                    am.motivo,
                    am.status,
                    am.motivo_nao_tomou,
                    am.data_baixa,
                    am.observacao,
                    f.nome AS funcionario_responsavel
                FROM agenda_medicamento am
                LEFT JOIN medicamento m
                    ON m.id = am.medicamento_id
                LEFT JOIN acolhido a
                    ON a.id = am.acolhido_id
                LEFT JOIN funcionario f
                    ON f.id = am.funcionario_responsavel_id
                ORDER BY am.data, am.horario
                """;

            case "patrimonio" -> """
                SELECT
                    p.tombamento,
                    p.especificacao,
                    p.dt_aquisicao
                FROM patrimonio p
                WHERE p.excluido = false
                ORDER BY p.tombamento
                """;

            case "eventos" -> """
                SELECT
                    e.nome,
                    e.data,
                    e.hora,
                    e.descricao,
                    (
                        SELECT STRING_AGG(
                            a.nome,
                            ', '
                            ORDER BY a.nome
                        )
                        FROM acolhido a
                        WHERE a.id = ANY(e.acolhidos)
                    ) AS acolhidos,
                    (
                        SELECT STRING_AGG(
                            f.nome,
                            ', '
                            ORDER BY f.nome
                        )
                        FROM funcionario f
                        WHERE f.id = ANY(e.responsaveis)
                    ) AS responsaveis
                FROM evento e
                WHERE e.excluido = false
                ORDER BY e.data, e.hora
                """;

            default -> null;
        };
    }

    private String obterArquivoJrxml(String tipo) {

        return switch (tipo) {

            case "acolhidos" ->
                    "acolhidos.jrxml";

            case "funcionarios" ->
                    "funcionarios.jrxml";

            case "medicamentos" ->
                    "medicamentos.jrxml";

            case "produtos" ->
                    "produtos.jrxml";

            case "estoqueMedicamentos" ->
                    "estoqueMedicamentos.jrxml";

            case "entradas" ->
                    "entradas.jrxml";

            case "saidas" ->
                    "saidas.jrxml";

            case "usoMedicamentos" ->
                    "usoMedicamentos.jrxml";

            case "agendaMedicamentos" ->
                    "agendaMedicamentos.jrxml";

            case "patrimonio" ->
                    "patrimonio.jrxml";

            case "eventos" ->
                    "eventos.jrxml";

            default -> null;
        };
    }
}