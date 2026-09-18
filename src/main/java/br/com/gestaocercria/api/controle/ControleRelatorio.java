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

        // Por enquanto vamos testar somente acolhidos
        if (!tipo.equals("acolhidos")) {
            return ResponseEntity.badRequest().build();
        }

        // SQL fixo
        String sql = """
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
                    .getResourceAsStream(
                        "relatorios/acolhidos.jasper"
                    );

        if (jasperInputStream == null) {
            throw new RuntimeException(
                "Arquivo acolhidos.jasper não encontrado."
            );
        }

        // Carrega o relatório já compilado
        JasperReport report =
                (JasperReport) JRLoader.loadObject(
                    jasperInputStream
                );

        // Preenche o relatório com os dados do banco
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
                    "inline; filename=relatorio-acolhidos.pdf"
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