import React, {useCallback} from 'react';
import {SafeAreaView, StyleSheet} from 'react-native';
import {CategoryButton} from '../../ui/Button';
import {useModal} from '../../hooks/useModal';
import ProductList from '../product/ProductList';
import GlobalModal from './../modal/GlobalModal';

const Home = () => {
  const {showModal} = useModal();

  const handleOpenCategoryModal = () => {
    showModal({
      modalType: 'HomeCategoryModal',
    });
  };

  return (
    <SafeAreaView style={styles.container}>
      <GlobalModal />
      <ProductList isSearch={false} />
      <CategoryButton minwidth={48} handlePress={handleOpenCategoryModal} />
    </SafeAreaView>
  );
};

export default Home;

const styles = StyleSheet.create({
  container: {
    flex: 1,
  },
});
