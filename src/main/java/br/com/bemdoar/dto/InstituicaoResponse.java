package br.com.bemdoar.dto;

import br.com.bemdoar.entity.Instituicao;

/** RF04 - dados publicos da instituicao. */
public record InstituicaoResponse(
        Long id, String nome, String descricao, String missao,
        String areaAtuacao, String publicoAtendido,
        String cep, String logradouro, String numero, String complemento,
        String bairro, String cidade, String estado,
        String telefone, String whatsapp, String email,
        String historia, String redesSociais, String horarioAtendimento, String logoUrl
) {
    public static InstituicaoResponse de(Instituicao i) {
        return new InstituicaoResponse(
                i.getId(), i.getNome(), i.getDescricao(), i.getMissao(),
                i.getAreaAtuacao(), i.getPublicoAtendido(),
                i.getCep(), i.getLogradouro(), i.getNumero(), i.getComplemento(),
                i.getBairro(), i.getCidade(), i.getEstado(),
                i.getTelefone(), i.getWhatsapp(), i.getEmail(),
                i.getHistoria(), i.getRedesSociais(), i.getHorarioAtendimento(), i.getLogoUrl());
    }
}
