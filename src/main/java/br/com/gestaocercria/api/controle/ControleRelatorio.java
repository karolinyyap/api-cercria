package br.com.gestaocercria.api.controle;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
@RestController
@RequestMapping("/relatorio")
public class ControleRelatorio {

    private final JdbcTemplate jdbcTemplate;

    public ControleRelatorio(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @PostMapping(
        value = "/gerar",
        consumes = MediaType.APPLICATION_JSON_VALUE,
        produces = MediaType.APPLICATION_PDF_VALUE
    )
    public ResponseEntity<byte[]> gerar(
            @RequestBody Map<String, Object> dados) {

        try {

            String tipo = string(dados.get("tipo"));

            if (tipo.isBlank()) {
                return ResponseEntity.badRequest().build();
            }

            /*
             * Campos selecionados no Angular
             */
            List<String> campos =
                    listaStrings(dados.get("campos"));

            if (campos.isEmpty()) {
                return ResponseEntity.badRequest().build();
            }

            /*
             * Confere se o tipo existe
             */
            if (!TIPOS.containsKey(tipo)) {
                return ResponseEntity.badRequest().build();
            }

            /*
             * Monta e executa o SQL
             */
            ResultadoSQL resultado =
                    executarSQL(
                        tipo,
                        campos,
                        mapa(dados.get("filtros")),
                        mapa(dados.get("ordenacao"))
                    );

            /*
             * Gera o JRXML dinamicamente
             */
            String jrxml =
                    criarJrxml(
                        tipo,
                        resultado.campos()
                    );

        JasperReport jasperReport;

        try {

            jasperReport =
                    JasperCompileManager.compileReport(
                            new ByteArrayInputStream(
                                    jrxml.getBytes(StandardCharsets.UTF_8)
                            )
                    );

        }  catch (Exception e) {
            System.out.println("========== ERRO AO COMPILAR JRXML ==========");

            Throwable causa = e;

            while (causa != null) {

                System.out.println("TIPO: " + causa.getClass().getName());
                System.out.println("MENSAGEM: " + causa.getMessage());

                causa = causa.getCause();

                System.out.println("--------------------------------------------");
            }

            System.out.println("============================================");

            throw e;
        }
            

            /*
             * Dados vindos diretamente do SQL
             */
            JRBeanCollectionDataSource dataSource =
                    new JRBeanCollectionDataSource(
                        resultado.registros()
                    );

            /*
             * Parâmetros do relatório
             */
            Map<String, Object> parametros =
                    new HashMap<>();

            parametros.put(
                "TITULO",
                nomeRelatorio(tipo)
            );

            parametros.put(
                "TOTAL",
                resultado.registros().size()
            );

            /*
             * Preenche o relatório
             */
            JasperPrint print =
                    JasperFillManager.fillReport(
                        jasperReport,
                        parametros,
                        dataSource
                    );

            /*
             * Converte para PDF
             */
            byte[] pdf =
                    JasperExportManager
                        .exportReportToPdf(print);

            return ResponseEntity.ok()
                    .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "inline; filename=relatorio-"
                            + tipo
                            + ".pdf"
                    )
                    .contentType(
                        MediaType.APPLICATION_PDF
                    )
                    .body(pdf);

        } catch (IllegalArgumentException e) {

            e.printStackTrace();

            return ResponseEntity.badRequest().build();

        } catch (Exception e) {

            e.printStackTrace();

            return ResponseEntity
                    .internalServerError()
                    .build();
        }
    }


    private static final Map<String, TipoConfig> TIPOS =
            criarTipos();


    private static Map<String, TipoConfig> criarTipos() {

        Map<String, TipoConfig> tipos =
                new HashMap<>();

        // --------------------------------------------------------
        // ACOLHIDOS
        // --------------------------------------------------------

        tipos.put(
            "acolhidos",
            new TipoConfig(
                "acolhido",
                Map.ofEntries(

                    Map.entry("id", "a.id"),
                    Map.entry("nome", "a.nome"),
                    Map.entry("cpf", "a.cpf"),
                    Map.entry(
                        "dataNascimento",
                        "a.data_nascimento"
                    ),
                    Map.entry("escola", "a.escola"),
                    Map.entry(
                        "localFamiliar",
                        "a.local_familiar"
                    ),
                    Map.entry(
                        "numeroProcesso",
                        "a.numero_processo"
                    ),
                    Map.entry("vara", "a.vara"),
                    Map.entry(
                        "dataEntrada",
                        "a.data_entrada"
                    ),
                    Map.entry(
                        "dataSaida",
                        "a.data_saida"
                    ),
                    Map.entry(
                        "corPele",
                        "a.cor_pele"
                    ),
                    Map.entry(
                        "deficiencia",
                        "a.deficiencia"
                    ),
                    Map.entry("ppcaam", "a.ppcaam")
                ),
                "a"
            )
        );


        // --------------------------------------------------------
        // MEDICAMENTOS
        // --------------------------------------------------------

        tipos.put(
            "medicamentos",
            new TipoConfig(
                "medicamento",
                Map.of(
                    "id",
                    "m.id",

                    "nome",
                    "m.nome",

                    "categoria",
                    "m.categoria",

                    "unidadeMedida",
                    "m.unidade_medida"
                ),
                "m"
            )
        );


        // --------------------------------------------------------
        // PRODUTOS
        // --------------------------------------------------------

        tipos.put(
            "produtos",
            new TipoConfig(
                "produto",
                Map.of(
                    "id",
                    "p.id",

                    "nome",
                    "p.nome",

                    "categoria",
                    "p.categoria",

                    "unidadeMedida",
                    "p.unidade_medida"
                ),
                "p"
            )
        );


        // --------------------------------------------------------
        // FUNCIONÁRIOS
        // --------------------------------------------------------

        tipos.put(
            "funcionarios",
            new TipoConfig(
                "funcionario",
                Map.ofEntries(

                    Map.entry("id", "f.id"),
                    Map.entry("nome", "f.nome"),
                    Map.entry("telefone", "f.telefone"),
                    Map.entry(
                        "dataNascimento",
                        "f.data_nascimento"
                    ),
                    Map.entry("email", "f.email"),
                    Map.entry("cpf", "f.cpf"),
                    Map.entry("rg", "f.rg"),
                    Map.entry(
                        "orgaoEmissor",
                        "f.orgao_emissor"
                    ),
                    Map.entry("uf", "f.uf"),
                    Map.entry("cargo", "f.cargo"),
                    Map.entry(
                        "escolaridade",
                        "f.escolaridade"
                    ),
                    Map.entry(
                        "cargaHoraria",
                        "f.carga_horaria"
                    ),
                    Map.entry("sexo", "f.sexo"),
                    Map.entry(
                        "dataAdmissao",
                        "f.data_admissao"
                    ),
                    Map.entry(
                        "dataSaida",
                        "f.data_saida"
                    )
                ),
                "f"
            )
        );


        // --------------------------------------------------------
        // ESTOQUE DE MEDICAMENTOS
        // --------------------------------------------------------

        tipos.put(
            "estoqueMedicamentos",
            new TipoConfig(
                "estoque_medicamento",
                Map.ofEntries(

                    Map.entry(
                        "id",
                        "e.id"
                    ),

                    Map.entry(
                        "medicamento",
                        "m.nome"
                    ),

                    Map.entry(
                        "categoria",
                        "m.categoria"
                    ),

                    Map.entry(
                        "quantidade",
                        "e.quantidade"
                    ),

                    Map.entry(
                        "quantidadeAtual",
                        "e.quantidade_atual"
                    ),

                    Map.entry(
                        "dataValidade",
                        "e.data_validade"
                    ),

                    Map.entry(
                        "origem",
                        "e.origem"
                    ),

                    Map.entry(
                        "responsavel",
                        "f.nome"
                    ),

                    Map.entry(
                        "dataEntrada",
                        "e.data_entrada"
                    )
                ),
                "e",
                """
                LEFT JOIN medicamento m
                    ON m.id = e.medicamento_id

                LEFT JOIN funcionario f
                    ON f.id = e.funcionario_id
                """
            )
        );


        // --------------------------------------------------------
        // ENTRADAS
        // --------------------------------------------------------

        tipos.put(
            "entradas",
            new TipoConfig(
                "entrada_produto",
                Map.ofEntries(

                    Map.entry(
                        "id",
                        "e.id"
                    ),

                    Map.entry(
                        "medicamento",
                        "p.nome"
                    ),

                    Map.entry(
                        "produto",
                        "p.nome"
                    ),

                    Map.entry(
                        "categoria",
                        "p.categoria"
                    ),

                    Map.entry(
                        "quantidade",
                        "e.quantidade"
                    ),

                    Map.entry(
                        "dataEntrada",
                        "e.data_entrada"
                    ),

                    Map.entry(
                        "dataValidade",
                        "e.data_validade"
                    ),

                    Map.entry(
                        "origem",
                        "e.origem"
                    ),

                    Map.entry(
                        "responsavel",
                        "f.nome"
                    ),

                    Map.entry(
                        "observacao",
                        "e.observacao"
                    ),

                    Map.entry(
                        "quantidadeAtual",
                        "e.quantidade_atual"
                    )
                ),
                "e",
                """
                LEFT JOIN produto p
                    ON p.id = e.produto_id

                LEFT JOIN funcionario f
                    ON f.id = e.funcionario_id
                """
            )
        );


        // --------------------------------------------------------
        // SAÍDAS
        // --------------------------------------------------------

        tipos.put(
            "saidas",
            new TipoConfig(
                "saida_produto",
                Map.ofEntries(

                    Map.entry(
                        "id",
                        "s.id"
                    ),

                    Map.entry(
                        "produto",
                        "p.nome"
                    ),

                    Map.entry(
                        "medicamento",
                        "p.nome"
                    ),

                    Map.entry(
                        "categoria",
                        "p.categoria"
                    ),

                    Map.entry(
                        "quantidade",
                        "s.quantidade"
                    ),

                    Map.entry(
                        "dataSaida",
                        "s.data_saida"
                    ),

                    Map.entry(
                        "motivo",
                        "s.motivo"
                    ),

                    Map.entry(
                        "responsavel",
                        "f.nome"
                    )
                ),
                "s",
                """
                LEFT JOIN produto p
                    ON p.id = s.produto_id

                LEFT JOIN funcionario f
                    ON f.id = s.funcionario_id
                """
            )
        );


        // --------------------------------------------------------
        // USO DE MEDICAMENTOS
        // --------------------------------------------------------

        tipos.put(
            "usoMedicamentos",
            new TipoConfig(
                "controle_uso_medicamento",
                Map.ofEntries(

                    Map.entry(
                        "id",
                        "c.id"
                    ),

                    Map.entry(
                        "medicamento",
                        "m.nome"
                    ),

                    Map.entry(
                        "acolhido",
                        "a.nome"
                    ),

                    Map.entry(
                        "dose",
                        "c.dose"
                    ),

                    Map.entry(
                        "intervalo",
                        "c.intervalo"
                    ),

                    Map.entry(
                        "iniciandoEm",
                        "c.iniciando_em"
                    ),

                    Map.entry(
                        "vezesAoDia",
                        "c.vezes_ao_dia"
                    ),

                    Map.entry(
                        "horarioFixo",
                        "c.horario_fixo"
                    ),

                    Map.entry(
                        "diasSemana",
                        "c.dias_semana"
                    ),

                    Map.entry(
                        "dataInicio",
                        "c.data_inicio"
                    ),

                    Map.entry(
                        "dataFim",
                        "c.data_fim"
                    ),

                    Map.entry(
                        "usoContinuo",
                        "c.uso_continuo"
                    ),

                    Map.entry(
                        "observacao",
                        "c.observacao"
                    ),

                    Map.entry(
                        "funcionarioCadastro",
                        "f.nome"
                    )
                ),
                "c",
                """
                LEFT JOIN medicamento m
                    ON m.id = c.medicamento_id

                LEFT JOIN acolhido a
                    ON a.id = c.acolhido_id

                LEFT JOIN funcionario f
                    ON f.id = c.funcionario_cadastro_id
                """
            )
        );


        // --------------------------------------------------------
        // AGENDA DE MEDICAMENTOS
        // --------------------------------------------------------

        tipos.put(
            "agendaMedicamentos",
            new TipoConfig(
                "agenda_medicamento",
                Map.ofEntries(

                    Map.entry(
                        "id",
                        "a.id"
                    ),

                    Map.entry(
                        "data",
                        "a.data"
                    ),

                    Map.entry(
                        "horario",
                        "a.horario"
                    ),

                    Map.entry(
                        "medicamento",
                        "m.nome"
                    ),

                    Map.entry(
                        "acolhido",
                        "ac.nome"
                    ),

                    Map.entry(
                        "dose",
                        "a.dose"
                    ),

                    Map.entry(
                        "motivo",
                        "a.motivo"
                    ),

                    Map.entry(
                        "status",
                        "a.status"
                    ),

                    Map.entry(
                        "motivoNaoTomou",
                        "a.motivo_nao_tomou"
                    ),

                    Map.entry(
                        "dataBaixa",
                        "a.data_baixa"
                    ),

                    Map.entry(
                        "observacao",
                        "a.observacao"
                    ),

                    Map.entry(
                        "funcionarioResponsavel",
                        "f.nome"
                    )
                ),
                "a",
                """
                LEFT JOIN medicamento m
                    ON m.id = a.medicamento_id

                LEFT JOIN acolhido ac
                    ON ac.id = a.acolhido_id

                LEFT JOIN funcionario f
                    ON f.id = a.funcionario_responsavel_id
                """
            )
        );


        // --------------------------------------------------------
        // PATRIMÔNIO
        // --------------------------------------------------------

        tipos.put(
            "patrimonio",
            new TipoConfig(
                "patrimonio",
                Map.ofEntries(

                    Map.entry(
                        "id",
                        "p.id"
                    ),

                    Map.entry(
                        "tombamento",
                        "p.tombamento"
                    ),

                    Map.entry(
                        "especificacao",
                        "p.especificacao"
                    ),

                    Map.entry(
                        "dtAquisicao",
                        "p.dt_aquisicao"
                    )
                ),
                "p"
            )
        );


        // --------------------------------------------------------
        // EVENTOS
        // --------------------------------------------------------

        tipos.put(
            "eventos",
            new TipoConfig(
                "evento",
                Map.ofEntries(

                    Map.entry(
                        "id",
                        "e.id"
                    ),

                    Map.entry(
                        "nome",
                        "e.nome"
                    ),

                    Map.entry(
                        "data",
                        "e.data"
                    ),

                    Map.entry(
                        "hora",
                        "e.hora"
                    ),

                    Map.entry(
                        "descricao",
                        "e.descricao"
                    ),
                    Map.entry(
                        "acolhidos",
                        """
                        (
                            SELECT STRING_AGG(
                                a.nome,
                                ', '
                                ORDER BY a.nome
                            )
                            FROM acolhido a
                            WHERE a.id = ANY(e.acolhidos)
                        )
                        """
                    ),

                    Map.entry(
                        "responsaveis",
                        """
                        (
                            SELECT STRING_AGG(
                                f.nome,
                                ', '
                                ORDER BY f.nome
                            )
                            FROM funcionario f
                            WHERE f.id = ANY(e.responsaveis)
                        )
                        """
                    )
                ),
                "e"
            )

            
        );

        return tipos;
    }


    // ============================================================
    // EXECUTAR SQL
    // ============================================================

    private ResultadoSQL executarSQL(
            String tipo,
            List<String> campos,
            Map<String, Object> filtros,
            Map<String, Object> ordenacao) {

        TipoConfig config =
                TIPOS.get(tipo);

        /*
         * --------------------------------------------------------
         * CAMPOS
         * --------------------------------------------------------
         */

        List<String> camposValidos =
                new ArrayList<>();

        for (String campo : campos) {

            if (config.campos().containsKey(campo)) {
                camposValidos.add(campo);
            }
        }

        if (camposValidos.isEmpty()) {

            throw new IllegalArgumentException(
                "Nenhum campo válido selecionado."
            );
        }


        /*
         * --------------------------------------------------------
         * SELECT
         * --------------------------------------------------------
         */

        StringBuilder sql =
                new StringBuilder("SELECT ");

        for (int i = 0;
             i < camposValidos.size();
             i++) {

            if (i > 0) {
                sql.append(", ");
            }

            String campo =
                    camposValidos.get(i);

            sql.append(
                config.campos().get(campo)
            );

            sql.append(" AS ");

            sql.append(campo);
        }

        sql.append(
            " FROM " + config.tabela() + " " + config.alias()
        );

        sql.append(" ");

        if (config.joins() != null) {
            sql.append(config.joins());
        }


        /*
         * --------------------------------------------------------
         * WHERE
         * --------------------------------------------------------
         */

        List<String> where =
                new ArrayList<>();

        List<Object> parametros =
                new ArrayList<>();

        adicionarCondicaoExcluido(
            tipo,
            config,
            where,
            parametros
        );

        adicionarFiltros(
            tipo,
            filtros,
            where,
            parametros
        );


        if (!where.isEmpty()) {

            sql.append(" WHERE ");

            sql.append(
                String.join(
                    " AND ",
                    where
                )
            );
        }


        /*
         * --------------------------------------------------------
         * ORDER BY
         * --------------------------------------------------------
         */

        String campoOrdenacao =
                string(
                    ordenacao.get("campo")
                );

        String ordem =
                string(
                    ordenacao.get("ordem")
                );

        if (!campoOrdenacao.isBlank()
                && config.campos()
                    .containsKey(campoOrdenacao)) {

            sql.append(" ORDER BY ");

            sql.append(
                config.campos()
                    .get(campoOrdenacao)
            );

            if ("desc".equalsIgnoreCase(ordem)) {
                sql.append(" DESC");
            } else {
                sql.append(" ASC");
            }
        }


        /*
         * --------------------------------------------------------
         * EXECUTA O SQL
         * --------------------------------------------------------
         */

        System.out.println(
            "\n========== RELATÓRIO =========="
        );

        System.out.println(
            "Tipo: " + tipo
        );

        System.out.println(
            "SQL: " + sql
        );

        System.out.println(
            "Parâmetros: " + parametros
        );

        System.out.println(
            "===============================\n"
        );


        List<Map<String, Object>> registros =
                jdbcTemplate.queryForList(
                    sql.toString(),
                    parametros.toArray()
                );

        return new ResultadoSQL(
            registros,
            camposValidos
        );
    }


    // ============================================================
    // FILTRO EXCLUIDO
    // ============================================================

    private void adicionarCondicaoExcluido(
            String tipo,
            TipoConfig config,
            List<String> where,
            List<Object> parametros) {

        switch (tipo) {

            case "acolhidos":
            case "funcionarios":
            case "medicamentos":
            case "produtos":
            case "patrimonio":
            case "eventos":

                where.add(
                    config.alias()
                    + ".excluido = false"
                );

                break;

            default:
                break;
        }
    }


    // ============================================================
    // FILTROS
    // ============================================================

    private void adicionarFiltros(
            String tipo,
            Map<String, Object> filtros,
            List<String> where,
            List<Object> parametros) {

        switch (tipo) {

            // ----------------------------------------------------
            // PRODUTOS
            // ----------------------------------------------------

            case "produtos":

                texto(
                    filtros,
                    "categoria",
                    "p.categoria",
                    where,
                    parametros
                );

                texto(
                    filtros,
                    "nome",
                    "p.nome",
                    where,
                    parametros
                );

                break;


            // ----------------------------------------------------
            // MEDICAMENTOS
            // ----------------------------------------------------

            case "medicamentos":

                texto(
                    filtros,
                    "categoria",
                    "m.categoria",
                    where,
                    parametros
                );

                texto(
                    filtros,
                    "nome",
                    "m.nome",
                    where,
                    parametros
                );

                break;


            // ----------------------------------------------------
            // ACOLHIDOS
            // ----------------------------------------------------

            case "acolhidos":

                texto(
                    filtros,
                    "nome",
                    "a.nome",
                    where,
                    parametros
                );

                texto(
                    filtros,
                    "cpf",
                    "a.cpf",
                    where,
                    parametros
                );

                texto(
                    filtros,
                    "escola",
                    "a.escola",
                    where,
                    parametros
                );

                data(
                    filtros,
                    "dataInicial",
                    "a.data_entrada",
                    ">=",
                    where,
                    parametros
                );

                data(
                    filtros,
                    "dataFinal",
                    "a.data_entrada",
                    "<=",
                    where,
                    parametros
                );

                break;


            // ----------------------------------------------------
            // FUNCIONÁRIOS
            // ----------------------------------------------------

            case "funcionarios":

                texto(
                    filtros,
                    "nome",
                    "f.nome",
                    where,
                    parametros
                );

                texto(
                    filtros,
                    "cpf",
                    "f.cpf",
                    where,
                    parametros
                );

                texto(
                    filtros,
                    "cargo",
                    "f.cargo",
                    where,
                    parametros
                );

                break;


            // ----------------------------------------------------
            // ESTOQUE
            // ----------------------------------------------------

            case "estoqueMedicamentos":

                texto(
                    filtros,
                    "categoria",
                    "m.categoria",
                    where,
                    parametros
                );

                texto(
                    filtros,
                    "origem",
                    "e.origem",
                    where,
                    parametros
                );

                dataTexto(
                    filtros,
                    "dataInicial",
                    "e.data_entrada",
                    ">=",
                    where,
                    parametros
                );

                dataTexto(
                    filtros,
                    "dataFinal",
                    "e.data_entrada",
                    "<=",
                    where,
                    parametros
                );

                break;


            // ----------------------------------------------------
            // ENTRADAS
            // ----------------------------------------------------

            case "entradas":

                texto(
                    filtros,
                    "categoria",
                    "p.categoria",
                    where,
                    parametros
                );

                dataTexto(
                    filtros,
                    "dataInicial",
                    "e.data_entrada",
                    ">=",
                    where,
                    parametros
                );

                dataTexto(
                    filtros,
                    "dataFinal",
                    "e.data_entrada",
                    "<=",
                    where,
                    parametros
                );

                break;


            // ----------------------------------------------------
            // SAÍDAS
            // ----------------------------------------------------

            case "saidas":

                texto(
                    filtros,
                    "motivo",
                    "s.motivo",
                    where,
                    parametros
                );

                dataTexto(
                    filtros,
                    "dataInicial",
                    "s.data_saida",
                    ">=",
                    where,
                    parametros
                );

                dataTexto(
                    filtros,
                    "dataFinal",
                    "s.data_saida",
                    "<=",
                    where,
                    parametros
                );

                break;


            // ----------------------------------------------------
            // USO DE MEDICAMENTOS
            // ----------------------------------------------------

            case "usoMedicamentos":

                dataTexto(
                    filtros,
                    "dataInicial",
                    "c.data_inicio",
                    ">=",
                    where,
                    parametros
                );

                dataTexto(
                    filtros,
                    "dataFinal",
                    "c.data_inicio",
                    "<=",
                    where,
                    parametros
                );

                break;


            // ----------------------------------------------------
            // AGENDA
            // ----------------------------------------------------

            case "agendaMedicamentos":

                texto(
                    filtros,
                    "status",
                    "a.status",
                    where,
                    parametros
                );

                dataTexto(
                    filtros,
                    "dataInicial",
                    "a.data",
                    ">=",
                    where,
                    parametros
                );

                dataTexto(
                    filtros,
                    "dataFinal",
                    "a.data",
                    "<=",
                    where,
                    parametros
                );

                break;


            // ----------------------------------------------------
            // PATRIMÔNIO
            // ----------------------------------------------------

            case "patrimonio":

                data(
                    filtros,
                    "dataInicial",
                    "p.dt_aquisicao",
                    ">=",
                    where,
                    parametros
                );

                data(
                    filtros,
                    "dataFinal",
                    "p.dt_aquisicao",
                    "<=",
                    where,
                    parametros
                );

                break;


            // ----------------------------------------------------
            // EVENTOS
            // ----------------------------------------------------

            case "eventos":

                data(
                    filtros,
                    "dataInicial",
                    "e.data",
                    ">=",
                    where,
                    parametros
                );

                data(
                    filtros,
                    "dataFinal",
                    "e.data",
                    "<=",
                    where,
                    parametros
                );

                break;

            default:
                break;
        }
    }


    // ============================================================
    // FILTRO TEXTO
    // ============================================================

    private void texto(
            Map<String, Object> filtros,
            String nomeFiltro,
            String coluna,
            List<String> where,
            List<Object> parametros) {

        String valor =
                string(
                    filtros.get(nomeFiltro)
                );

        if (!valor.isBlank()) {

            where.add(
                "LOWER(CAST("
                + coluna
                + " AS TEXT)) LIKE LOWER(?)"
            );

            parametros.add(
                "%" + valor.trim() + "%"
            );
        }
    }


    // ============================================================
    // FILTRO DATA
    // ============================================================

    private void data(
            Map<String, Object> filtros,
            String nomeFiltro,
            String coluna,
            String operador,
            List<String> where,
            List<Object> parametros) {

        String valor =
                string(
                    filtros.get(nomeFiltro)
                );

        if (!valor.isBlank()) {

            where.add(
                coluna
                + " "
                + operador
                + " ?"
            );

            parametros.add(
                Date.valueOf(valor)
            );
        }
    }


    // ============================================================
    // FILTRO DATA ARMAZENADA COMO STRING
    // ============================================================

    private void dataTexto(
            Map<String, Object> filtros,
            String nomeFiltro,
            String coluna,
            String operador,
            List<String> where,
            List<Object> parametros) {

        String valor =
                string(
                    filtros.get(nomeFiltro)
                );

        if (!valor.isBlank()) {

            /*
             * As entidades EntradaProduto,
             * SaidaProduto, EstoqueMedicamento,
             * AgendaMedicamento etc. possuem
             * algumas datas como String.
             *
             * O sistema Angular trabalha com
             * yyyy-MM-dd, portanto podemos
             * comparar como texto.
             */
            where.add(
                coluna
                + " "
                + operador
                + " ?"
            );

            parametros.add(valor);
        }
    }


    // ============================================================
    // JRXML DINÂMICO
    // ============================================================

    private String criarJrxml(
            String tipo,
            List<String> campos) {

        int totalCampos = campos.size();

        int larguraPagina = 802;

        int larguraCampo =
                Math.max(
                        50,
                        larguraPagina / totalCampos
                );

        StringBuilder xml = new StringBuilder();

        /*
        * ============================================================
        * INÍCIO DO RELATÓRIO
        * ============================================================
        *
        * Não colocamos <?xml ... ?> aqui.
        * O Jasper consegue interpretar o JRXML sem essa declaração.
        */
        xml.append("""
            <jasperReport
                xmlns="http://jasperreports.sourceforge.net/jasperreports"
                xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                xsi:schemaLocation="
                    http://jasperreports.sourceforge.net/jasperreports
                    http://jasperreports.sourceforge.net/xsd/jasperreport.xsd"
                name="relatorioDinamico"
                pageWidth="842"
                pageHeight="595"
                orientation="Landscape"
                columnWidth="802"
                leftMargin="20"
                rightMargin="20"
                topMargin="20"
                bottomMargin="20">

        """);

        /*
        * ============================================================
        * PARÂMETROS
        * ============================================================
        *
        * Os parâmetros precisam aparecer antes dos fields.
        */
        xml.append("""
            <parameter
                name="TITULO"
                class="java.lang.String"/>

            <parameter
                name="TOTAL"
                class="java.lang.Integer"/>

        """);

        /*
        * ============================================================
        * CAMPOS
        * ============================================================
        */
        for (String campo : campos) {

            xml.append("""
                <field
                    name="%s"
                    class="java.lang.Object"/>

            """.formatted(
                    escaparXml(campo)
            ));
        }

        /*
        * ============================================================
        * TÍTULO
        * ============================================================
        */
        xml.append("""
            <title>

                <band height="65">

                    <textField>

                        <reportElement
                            x="0"
                            y="0"
                            width="802"
                            height="35"/>

                        <textElement
                            textAlignment="Center"
                            verticalAlignment="Middle"/>

                        <textFieldExpression>
                            <![CDATA[$P{TITULO}]]>
                        </textFieldExpression>

                    </textField>

                    <textField>

                        <reportElement
                            x="0"
                            y="40"
                            width="802"
                            height="20"/>

                        <textElement
                            textAlignment="Center"
                            verticalAlignment="Middle"/>

                        <textFieldExpression>
                            <![CDATA["Total de registros: " + $P{TOTAL}]]>
                        </textFieldExpression>

                    </textField>

                </band>

            </title>

        """);

        /*
        * ============================================================
        * CABEÇALHO DAS COLUNAS
        * ============================================================
        */
        xml.append("""
            <columnHeader>

                <band height="35">

        """);

        int x = 0;

        for (String campo : campos) {

            xml.append("""
                <staticText>

                    <reportElement
                        x="%d"
                        y="0"
                        width="%d"
                        height="35"/>

                    <box>
                        <pen lineWidth="1"/>
                    </box>

                    <textElement
                        textAlignment="Center"
                        verticalAlignment="Middle"/>

                    <text>
                        <![CDATA[%s]]>
                    </text>

                </staticText>

            """.formatted(
                    x,
                    larguraCampo,
                    escaparXml(nomeCampo(campo))
            ));

            x += larguraCampo;
        }

        xml.append("""
                </band>

            </columnHeader>

        """);

        /*
        * ============================================================
        * DETALHES / LINHAS DO RELATÓRIO
        * ============================================================
        */
        xml.append("""
            <detail>

                <band height="30">

        """);

        x = 0;

        for (String campo : campos) {

            xml.append("""
                <textField
                    isBlankWhenNull="true">

                    <reportElement
                        x="%d"
                        y="0"
                        width="%d"
                        height="30"/>

                    <box>
                        <pen lineWidth="0.5"/>
                    </box>

                    <textElement
                        textAlignment="Center"
                        verticalAlignment="Middle"/>

                    <textFieldExpression>
                        <![CDATA[$F{%s}]]>
                    </textFieldExpression>

                </textField>

            """.formatted(
                    x,
                    larguraCampo,
                    escaparXml(campo)
            ));

            x += larguraCampo;
        }

        xml.append("""
                </band>

            </detail>

        """);

        /*
        * ============================================================
        * RODAPÉ
        * ============================================================
        */
        xml.append("""
            <pageFooter>

                <band height="25">

                    <textField>

                        <reportElement
                            x="0"
                            y="0"
                            width="802"
                            height="20"/>

                        <textElement
                            textAlignment="Right"
                            verticalAlignment="Middle"/>

                        <textFieldExpression>
                            <![CDATA["Página " + $V{PAGE_NUMBER}]]>
                        </textFieldExpression>

                    </textField>

                </band>

            </pageFooter>

        """);

        /*
        * ============================================================
        * FIM DO RELATÓRIO
        * ============================================================
        */
        xml.append("""
            </jasperReport>
        """);

        return xml.toString();
    }


    // ============================================================
    // NOME DOS CAMPOS
    // ============================================================

    private String nomeCampo(
            String campo) {

        return switch (campo) {

            case "id" ->
                "Código";

            case "nome" ->
                "Nome";

            case "cpf" ->
                "CPF";

            case "dataNascimento" ->
                "Data de nascimento";

            case "escola" ->
                "Escola";

            case "localFamiliar" ->
                "Local familiar";

            case "numeroProcesso" ->
                "Número do processo";

            case "vara" ->
                "Vara";

            case "dataEntrada" ->
                "Data de entrada";

            case "dataSaida" ->
                "Data de saída";

            case "corPele" ->
                "Cor da pele";

            case "deficiencia" ->
                "Deficiência";

            case "ppcaam" ->
                "PPCAAM";

            case "medicamento" ->
                "Medicamento";

            case "produto" ->
                "Produto";

            case "categoria" ->
                "Categoria";

            case "unidadeMedida" ->
                "Unidade de medida";

            case "quantidade" ->
                "Quantidade";

            case "quantidadeAtual" ->
                "Quantidade atual";

            case "dataValidade" ->
                "Data de validade";

            case "origem" ->
                "Origem";

            case "responsavel" ->
                "Responsável";

            case "observacao" ->
                "Observação";

            case "motivo" ->
                "Motivo";

            case "dose" ->
                "Dose";

            case "intervalo" ->
                "Intervalo";

            case "iniciandoEm" ->
                "Iniciando em";

            case "vezesAoDia" ->
                "Vezes ao dia";

            case "horarioFixo" ->
                "Horário fixo";

            case "diasSemana" ->
                "Dias da semana";

            case "dataInicio" ->
                "Data de início";

            case "dataFim" ->
                "Data de fim";

            case "usoContinuo" ->
                "Uso contínuo";

            case "acolhido" ->
                "Acolhido";

            case "status" ->
                "Status";

            case "motivoNaoTomou" ->
                "Motivo de não tomar";

            case "dataBaixa" ->
                "Data da baixa";

            case "funcionarioResponsavel" ->
                "Responsável";

            case "funcionarioCadastro" ->
                "Funcionário";

            case "telefone" ->
                "Telefone";

            case "email" ->
                "E-mail";

            case "cargo" ->
                "Cargo";

            case "escolaridade" ->
                "Escolaridade";

            case "cargaHoraria" ->
                "Carga horária";

            case "sexo" ->
                "Sexo";

            case "dataAdmissao" ->
                "Data de admissão";

            case "rg" ->
                "RG";

            case "orgaoEmissor" ->
                "Órgão emissor";

            case "uf" ->
                "UF";

            case "tombamento" ->
                "Tombamento";

            case "especificacao" ->
                "Especificação";

            case "dtAquisicao" ->
                "Data de aquisição";

            case "data" ->
                "Data";

            case "horario" ->
                "Horário";

            case "descricao" ->
                "Descrição";

            case "acolhidos" ->
                "Acolhidos";

            case "responsaveis" ->
                "Responsáveis";

            default ->
                campo;
        };
    }


    // ============================================================
    // TÍTULO
    // ============================================================

    private String nomeRelatorio(
            String tipo) {

        return switch (tipo) {

            case "acolhidos" ->
                "Relatório de Acolhidos";

            case "medicamentos" ->
                "Relatório de Medicamentos";

            case "produtos" ->
                "Relatório de Produtos";

            case "funcionarios" ->
                "Relatório de Funcionários";

            case "estoqueMedicamentos" ->
                "Relatório de Estoque de Medicamentos";

            case "entradas" ->
                "Relatório de Entradas de Produtos";

            case "saidas" ->
                "Relatório de Saídas de Produtos";

            case "usoMedicamentos" ->
                "Relatório de Uso de Medicamentos";

            case "agendaMedicamentos" ->
                "Relatório da Agenda de Medicamentos";

            case "patrimonio" ->
                "Relatório de Patrimônio";

            case "eventos" ->
                "Relatório de Eventos";

            default ->
                "Relatório";
        };
    }


    // ============================================================
    // CLASSES AUXILIARES
    // ============================================================

    private record TipoConfig(
            String tabela,
            Map<String, String> campos,
            String alias,
            String joins) {

        TipoConfig(
                String tabela,
                Map<String, String> campos,
                String alias) {

            this(
                tabela,
                campos,
                alias,
                ""
            );
        }
    }


    private record ResultadoSQL(
            List<Map<String, Object>> registros,
            List<String> campos) {
    }


    // ============================================================
    // UTILITÁRIOS
    // ============================================================

    @SuppressWarnings("unchecked")
    private Map<String, Object> mapa(
            Object valor) {

        if (valor instanceof Map<?, ?>) {

            return (Map<String, Object>) valor;
        }

        return new HashMap<>();
    }


    private List<String> listaStrings(
            Object valor) {

        List<String> resultado =
                new ArrayList<>();

        if (valor instanceof Collection<?> lista) {

            for (Object item : lista) {

                if (item != null) {

                    resultado.add(
                        item.toString()
                    );
                }
            }
        }

        return resultado;
    }


    private String string(
            Object valor) {

        if (valor == null) {
            return "";
        }

        return valor.toString();
    }


    private String escaparXml(
            String texto) {

        if (texto == null) {
            return "";
        }

        return texto
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&apos;");
    }
}