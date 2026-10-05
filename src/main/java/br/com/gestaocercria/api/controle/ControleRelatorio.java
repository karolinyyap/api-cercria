package br.com.gestaocercria.api.controle;

import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRMapCollectionDataSource;
import net.sf.jasperreports.engine.util.JRLoader;

import org.springframework.http.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.io.InputStream;
import java.util.*;

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

            // Define o SQL de acordo com o tipo do relatório
            String sql;
            String arquivoJasper;
            String nomeArquivo;

            switch (tipo) {

                case "acolhidos":

                    sql = """
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
                            a.ppcaam,
                            a.alergias
                        FROM acolhido a
                        ORDER BY a.nome
                        """;

                    arquivoJasper = "relatorios/acolhidos.jasper";
                    nomeArquivo = "relatorio-acolhidos.pdf";

                    break;

                case "funcionarios":

                    sql = """
                        SELECT
                            f.nome,
                            f.cpf,
                            f.rg,
                            f.orgao_emissor,
                            f.uf,
                            f.telefone,
                            f.email,
                            f.data_nascimento,
                            f.cargo,
                            f.escolaridade,
                            f.carga_horaria,
                            f.sexo,
                            f.data_admissao,
                            f.data_saida,
                            f.turno,
                            f.tipoContrato,
                            f.dataFimContrato
                        FROM funcionario f
                        ORDER BY f.nome
                        """;

                    arquivoJasper = "relatorios/funcionarios.jasper";
                    nomeArquivo = "relatorio-funcionarios.pdf";

                    break;
                case "evento":
                    sql = """
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
                        ORDER BY e.data, e.hora, e.nome
                        """;

                    arquivoJasper = "relatorios/evento.jasper";
                    nomeArquivo = "relatorio-evento.pdf";

                    break;

                case "produto":
                    sql = """
                        SELECT
                            p.nome,
                            p.categoria,
                            p.unidade_medida
                        FROM produto p
                        WHERE p.excluido = false
                        ORDER BY p.nome
                        """;

                    arquivoJasper = "relatorios/produto.jasper";
                    nomeArquivo = "relatorio-produto.pdf";

                    break;

                case "patrimonio":

                    sql = """
                        SELECT
                            p.tombamento,
                            p.especificacao,
                            p.dt_aquisicao
                        FROM patrimonio p
                        WHERE p.excluido = false
                        ORDER BY p.tombamento
                        """;

                    arquivoJasper = "relatorios/patrimonio.jasper";
                    nomeArquivo = "relatorio-patrimonio.pdf";

                    break;
                
                case "medicamento":
                    sql = """
                        SELECT
                            m.nome,
                            m.categoria,
                            m.unidade_medida
                        FROM medicamento m
                        ORDER BY m.nome
                        """;

                    arquivoJasper = "relatorios/medicamento.jasper";
                    nomeArquivo = "relatorio-medicamento.pdf";

                    break;

                default:

                    return ResponseEntity
                            .badRequest()
                            .build();
            }

            // Executa o SQL
            List<Map<String, Object>> resultado =
                    jdbcTemplate.queryForList(sql);

            // Converte para o formato aceito pelo Jasper
            List<Map<String, ?>> dadosJasper =
                    new ArrayList<>(resultado);

            JRMapCollectionDataSource dataSource =
                    new JRMapCollectionDataSource(dadosJasper);

            // Localiza o arquivo .jasper
            InputStream jasperInputStream =
                    getClass()
                            .getClassLoader()
                            .getResourceAsStream(arquivoJasper);

            if (jasperInputStream == null) {
                throw new RuntimeException(
                        "Arquivo " + arquivoJasper + " não encontrado."
                );
            }

            // Carrega o relatório compilado
            JasperReport report =
                    (JasperReport) JRLoader.loadObject(
                            jasperInputStream
                    );

            // Preenche o relatório
            JasperPrint print =
                    JasperFillManager.fillReport(
                            report,
                            new HashMap<>(),
                            dataSource
                    );

            // Gera o PDF
            byte[] pdf =
                    JasperExportManager.exportReportToPdf(print);

            return ResponseEntity.ok()
                    .header(
                            HttpHeaders.CONTENT_DISPOSITION,
                            "inline; filename=" + nomeArquivo
                    )
                    .contentType(
                            MediaType.APPLICATION_PDF
                    )
                    .body(pdf);

        } catch (Exception e) {

            e.printStackTrace();

            return ResponseEntity
                    .internalServerError()
                    .build();
        }
    }
}