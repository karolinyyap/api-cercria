package br.com.gestaocercria.api.controle;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRMapCollectionDataSource;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.io.InputStream;
import java.util.ArrayList;
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

            String sql = """
                SELECT
                    nome,
                    cpf,
                    escola,
                    data_entrada
                FROM acolhido
                WHERE excluido = false
                ORDER BY nome
                """;

            List<Map<String, Object>> resultado =
                    jdbcTemplate.queryForList(sql);

            List<Map<String, ?>> dadosJasper =
                    new ArrayList<>(resultado);

            JRMapCollectionDataSource dataSource =
                    new JRMapCollectionDataSource(dadosJasper);

            InputStream jrxml =
                    getClass()
                    .getClassLoader()
                    .getResourceAsStream(
                        "relatorios/acolhidos.jrxml"
                    );

            if (jrxml == null) {
                throw new RuntimeException(
                    "acolhidos.jrxml não encontrado"
                );
            }

            JasperReport report =
                    JasperCompileManager.compileReport(jrxml);

            JasperPrint print =
                    JasperFillManager.fillReport(
                        report,
                        Map.of(),
                        dataSource
                    );

            byte[] pdf =
                    JasperExportManager.exportReportToPdf(print);

            return ResponseEntity.ok()
                    .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "inline; filename=acolhidos.pdf"
                    )
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(pdf);

        } catch (Exception e) {

            e.printStackTrace();

            return ResponseEntity
                    .internalServerError()
                    .build();
        }
    }
}