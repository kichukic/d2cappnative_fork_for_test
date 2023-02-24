import express from 'express';
import controller from '../controller/Product';

const router = express.Router();

router.post('/productcreate', controller.productcreate);
router.get('/productget/:productId', controller.productget);
router.get('/productgetall', controller.productgetall);
router.patch('/productupdate/:productId', controller.productupdate);
router.delete('/productdelete/:productId', controller.productdelete);


export = router;
