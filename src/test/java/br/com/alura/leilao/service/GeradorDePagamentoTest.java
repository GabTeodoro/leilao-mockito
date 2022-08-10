package br.com.alura.leilao.service;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;

import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import br.com.alura.leilao.dao.PagamentoDao;
import br.com.alura.leilao.model.Lance;
import br.com.alura.leilao.model.Leilao;
import br.com.alura.leilao.model.Pagamento;
import br.com.alura.leilao.model.Usuario;

class GeradorDePagamentoTest {
	
	private GeradorDePagamento service;
	
	@Mock
	private PagamentoDao pagamentoDao;
	
	@Mock
	private Clock clock;
	
	@Captor
	private ArgumentCaptor<Pagamento> captor;
	
	public GeradorDePagamentoTest() {
		MockitoAnnotations.initMocks(this);	
		this.service = new GeradorDePagamento(pagamentoDao, clock);
	}

	@Test
	void deveriaCriarPagementoParaVencendor() {
		
		Leilao leilao = leilao();
		Lance lance = leilao.getLanceVencedor();
		
		// Mock do LocalDate.now() / Mocando o dia 
		LocalDate hoje = LocalDate.of(2022, 8, 9);
		Instant instant = hoje.atStartOfDay(ZoneId.systemDefault()).toInstant();
		Mockito.when(clock.instant()).thenReturn(instant);
		Mockito.when(clock.getZone()).thenReturn(ZoneId.systemDefault());
		
		service.gerarPagamento(lance);
		
		Mockito.verify(pagamentoDao).salvar(captor.capture());
		Pagamento pagamento = captor.getValue();
		
		Assert.assertEquals(LocalDate.now().plusDays(1), pagamento.getVencimento());
		Assert.assertEquals(lance.getValor(), pagamento.getValor());
		Assert.assertFalse(pagamento.getPago());
		Assert.assertEquals(lance.getUsuario(), pagamento.getUsuario());
		Assert.assertEquals(leilao, pagamento.getLeilao());
		
	}
	
	private Leilao leilao() {
		
        Leilao leilao = new Leilao("Celular",
                        new BigDecimal("500"),
                        new Usuario("Fulano"));

        Lance lance = new Lance(new Usuario("Ciclano"),
                        new BigDecimal("900"));

        leilao.propoe(lance);
        leilao.setLanceVencedor(lance);
        return leilao;

    }

}
