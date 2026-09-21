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
                            a.ppcaam
                        FROM acolhido a
                        WHERE a.excluido = false
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
                            f.data_saida
                        FROM funcionario f
                        WHERE f.excluido = false
                        ORDER BY f.nome
                        """;

                    arquivoJasper = "relatorios/funcionarios.jasper";
                    nomeArquivo = "relatorio-funcionarios.pdf";

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