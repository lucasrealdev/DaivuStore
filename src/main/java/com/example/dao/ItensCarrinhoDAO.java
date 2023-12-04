package com.example.dao;

import com.example.entity.Produto;
import com.example.entity.ProdutoCart;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;


public class ItensCarrinhoDAO {
    private final ProdutoDAO produtos = new ProdutoDAO(); // Instância de ProdutoDAO para buscar detalhes do produto


    // Método para obter todos os produtos do carrinho do usuário com base no ID do usuário
    public ArrayList<ProdutoCart> getAllProdutos(int idUser) {
        ArrayList<ProdutoCart> produtosCarrinhoDB = new ArrayList<>(); // Lista para armazenar os produtos do carrinho
        String sql = "SELECT produto_id, quantidade FROM itenscarrinho WHERE usuario_id = ?";
        try (Connection connection = ConnectionDb.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, idUser); // Define o ID do usuário no PreparedStatement
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    long produto_id = resultSet.getInt("produto_id"); // Obtém o ID do produto do ResultSet
                    int quantidade = resultSet.getInt("quantidade"); // Obtém a quantidade do ResultSet
                    Produto produto = produtos.get(produto_id); // Busca o detalhe do produto pelo ID
                    ProdutoCart produtoCart = new ProdutoCart(produto, quantidade); // Cria um ProdutoCart com o produto e a quantidade
                    produtosCarrinhoDB.add(produtoCart); // Adiciona o ProdutoCart à lista de produtos do carrinho
                }
            }
        } catch (SQLException e) {
            System.out.println("Problema nos itens Carrinhos, nao foi possivel!");
            e.printStackTrace(); // Imprime detalhes da exceção se houver um erro
        }
        return produtosCarrinhoDB; // Retorna a lista de produtos do carrinho
    }

    public void save(ProdutoCart produtoCart, int idUser) {
        String sql = "INSERT INTO itenscarrinho (usuario_id, produto_id, quantidade) VALUES (?, ?, ?)";
        try (Connection connection = ConnectionDb.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            // Define os parâmetros no PreparedStatement para a inserção na tabela 'itenscarrinho'
            statement.setInt(1, idUser); // Define o ID do usuário
            statement.setInt(2, Math.toIntExact(produtoCart.getProduto().getId())); // Define o ID do produto
            statement.setInt(3, produtoCart.getQuantidadeProduto()); // Define a quantidade do produto

            statement.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Nao foi possivel salvar");
            e.printStackTrace(); // Imprime detalhes da exceção se houver um erro
        }
    }
    public void update(ProdutoCart produtoCart, int idUser) {
        String sql = "UPDATE itenscarrinho SET quantidade = ? WHERE usuario_id = ? AND produto_id = ?";

        try (Connection connection = ConnectionDb.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            // Define os parâmetros no PreparedStatement para a atualização na tabela 'itenscarrinho'
            statement.setInt(1, produtoCart.getQuantidadeProduto()); // Define a nova quantidade do produto
            statement.setInt(2, idUser); // Define o ID do usuário
            statement.setInt(3, Math.toIntExact(produtoCart.getProduto().getId())); // Define o ID do produto

            statement.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Não foi possível atualizar");
            e.printStackTrace(); // Imprime detalhes da exceção se houver um erro
        }
    }

    public void remove(int idUser, int idProduct) {
        String sql = "DELETE FROM itenscarrinho WHERE usuario_id = ? AND produto_id = ?";

        try (Connection connection = ConnectionDb.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            // Define os parâmetros no PreparedStatement para a inserção na tabela 'itenscarrinho'
            statement.setInt(1, idUser); // Define o ID do usuário
            statement.setInt(2, idProduct); // Define o ID do produto

            statement.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Nao foi removido o item do carrinho");
            e.printStackTrace(); // Imprime detalhes da exceção se houver um erro
        }
    }

    public void removeAll(int idUser) {
        String sql = "DELETE FROM itenscarrinho WHERE usuario_id = ?";

        try (Connection connection = ConnectionDb.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            // Define os parâmetros no PreparedStatement para a inserção na tabela 'itenscarrinho'
            statement.setInt(1, idUser); // Define o ID do usuário


            statement.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Erro na execução, não foi deletado todos os itens!");
            e.printStackTrace(); // Imprime detalhes da exceção se houver um erro
        }
    }
}