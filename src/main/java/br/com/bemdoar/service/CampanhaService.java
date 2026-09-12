package br.com.bemdoar.service;
import br.com.bemdoar.dto.*; import br.com.bemdoar.entity.*; import br.com.bemdoar.enums.*; import br.com.bemdoar.exception.*; import br.com.bemdoar.repository.*;
import org.springframework.stereotype.Service; import org.springframework.transaction.annotation.Transactional; import java.util.*;
@Service public class CampanhaService {
 private final CampanhaRepository repo; private final DoacaoRepository doacoes; private final NecessidadeService necessidades; private final NotificacaoService notificacoes;
 public CampanhaService(CampanhaRepository r,DoacaoRepository d,NecessidadeService n,NotificacaoService no){repo=r;doacoes=d;necessidades=n;notificacoes=no;}
 private Campanha get(Long id){return repo.findById(id).orElseThrow(()->new RecursoNaoEncontradoException("Campanha nao encontrada."));}
 private CampanhaResponse out(Campanha c){return CampanhaResponse.de(c,doacoes.somarRecebidasDaCampanha(c.getId()));}
 @Transactional(readOnly=true) public List<CampanhaResponse> listar(){return repo.findAllByOrderByDataInicioDesc().stream().map(this::out).toList();}
 @Transactional(readOnly=true) public CampanhaResponse buscar(Long id){return out(get(id));}
 @Transactional public CampanhaResponse criar(CampanhaRequest q){validar(q); Campanha c=new Campanha(); copiar(c,q); c.setSituacao(SituacaoCampanha.PLANEJADA); return out(repo.save(c));}
 @Transactional public CampanhaResponse editar(Long id,CampanhaRequest q){Campanha c=get(id); if(c.getSituacao()==SituacaoCampanha.ENCERRADA||c.getSituacao()==SituacaoCampanha.CANCELADA) throw new RegraNegocioException("Esta campanha nao pode mais ser editada."); validar(q); copiar(c,q); return out(repo.save(c));}
 @Transactional public CampanhaResponse ativar(Long id){Campanha c=get(id); if(c.getSituacao()!=SituacaoCampanha.PLANEJADA) invalida(); c.setSituacao(SituacaoCampanha.ATIVA); repo.save(c); notificacoes.notificarTodosUsuarios("Nova campanha: "+c.getTitulo()); return out(c);}
 @Transactional public CampanhaResponse encerrar(Long id){Campanha c=get(id); if(c.getSituacao()!=SituacaoCampanha.PLANEJADA&&c.getSituacao()!=SituacaoCampanha.ATIVA) invalida(); c.setSituacao(SituacaoCampanha.ENCERRADA); return out(repo.save(c));}
 @Transactional public CampanhaResponse cancelar(Long id,CancelamentoRequest q){Campanha c=get(id); if(c.getSituacao()!=SituacaoCampanha.PLANEJADA&&c.getSituacao()!=SituacaoCampanha.ATIVA) invalida(); if(q==null||q.motivo()==null||q.motivo().isBlank()) throw new RegraNegocioException("Informe o motivo do cancelamento."); c.setMotivoCancelamento(q.motivo()); c.setSituacao(SituacaoCampanha.CANCELADA); return out(repo.save(c));}
 @Transactional public CampanhaResponse vincular(Long id,Long necId){Campanha c=get(id); Necessidade n=necessidades.buscarDisponivelOuFalhar(necId); if(c.getNecessidades().contains(n)) throw new RegraNegocioException("Este vinculo ja existe."); c.getNecessidades().add(n); return out(repo.save(c));}
 @Transactional public CampanhaResponse desvincular(Long id,Long necId){Campanha c=get(id); c.getNecessidades().removeIf(n->n.getId().equals(necId)); return out(repo.save(c));}
 @Transactional(readOnly=true) public Campanha buscarDisponivelOuFalhar(Long id){Campanha c=get(id); if(c.getSituacao()!=SituacaoCampanha.ATIVA) throw new RegraNegocioException("Este destino nao esta recebendo novas contribuicoes."); return c;}
 private void validar(CampanhaRequest q){if(q.dataFim().isBefore(q.dataInicio())) throw new RegraNegocioException("A data final nao pode ser anterior a inicial.");}
 private void copiar(Campanha c,CampanhaRequest q){c.setTitulo(q.titulo());c.setDescricao(q.descricao());c.setObjetivo(q.objetivo());c.setImagemUrl(q.imagemUrl());c.setDataInicio(q.dataInicio());c.setDataFim(q.dataFim());c.setMetaMinima(q.metaMinima());}
 private void invalida(){throw new RegraNegocioException("Esta transicao nao e permitida.");}
}
